package com.example.student.service;

import com.example.student.dto.request.StudentRequest;
import com.example.student.dto.response.StudentResponse;
import com.example.student.entity.Clazz;
import com.example.student.entity.Role;
import com.example.student.entity.Student;
import com.example.student.entity.User;
import com.example.student.exception.BusinessException;
import com.example.student.repository.*;
import com.example.student.util.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询学生
     */
    public PageVO<StudentResponse> findPage(Integer page, Integer size, String keyword, 
                                            Long classId, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Student> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                Predicate nameLike = cb.like(root.get("name"), "%" + keyword + "%");
                Predicate studentNoLike = cb.like(root.get("studentNo"), "%" + keyword + "%");
                predicates.add(cb.or(nameLike, studentNoLike));
            }
            if (classId != null) {
                predicates.add(cb.equal(root.get("clazz").get("id"), classId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Student> pageResult = studentRepository.findAll(spec, pageable);
        List<StudentResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有学生
     */
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .filter(s -> !s.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据班级ID获取学生列表
     */
    public List<StudentResponse> findByClassId(Long classId) {
        return studentRepository.findByClazzId(classId).stream()
                .filter(s -> !s.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询学生
     */
    public StudentResponse findById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("学生不存在"));
        return toResponse(student);
    }

    /**
     * 根据学号查询学生
     */
    public StudentResponse findByStudentNo(String studentNo) {
        Student student = studentRepository.findByStudentNo(studentNo)
                .orElseThrow(() -> BusinessException.notFound("学生不存在"));
        return toResponse(student);
    }

    /**
     * 创建学生
     */
    @Transactional
    public StudentResponse create(StudentRequest request) {
        // 检查学号是否存在
        if (studentRepository.existsByStudentNo(request.getStudentNo())) {
            throw BusinessException.badRequest("学号已存在");
        }

        Student student = new Student();
        copyProperties(request, student);

        // 设置班级
        if (request.getClassId() != null) {
            Clazz clazz = clazzRepository.findById(request.getClassId())
                    .orElseThrow(() -> BusinessException.notFound("班级不存在"));
            student.setClazz(clazz);
            // 更新班级学生人数
            clazz.setStudentCount(clazz.getStudentCount() + 1);
            clazzRepository.save(clazz);
        }

        // 自动创建登录账号
        if (request.getCreateAccount() != null && request.getCreateAccount()) {
            User user = createUserAccount(request.getStudentNo(), request.getName(), request.getEmail());
            student.setUser(user);
        }

        student = studentRepository.save(student);
        log.info("创建学生成功: {}", student.getName());
        return toResponse(student);
    }

    /**
     * 更新学生
     */
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("学生不存在"));

        // 检查学号是否被其他学生使用
        if (!student.getStudentNo().equals(request.getStudentNo()) &&
            studentRepository.existsByStudentNo(request.getStudentNo())) {
            throw BusinessException.badRequest("学号已存在");
        }

        // 处理班级变更
        Long oldClassId = student.getClazz() != null ? student.getClazz().getId() : null;
        Long newClassId = request.getClassId();

        copyProperties(request, student);

        // 设置班级
        if (newClassId != null) {
            Clazz newClazz = clazzRepository.findById(newClassId)
                    .orElseThrow(() -> BusinessException.notFound("班级不存在"));
            student.setClazz(newClazz);

            // 更新班级学生人数
            if (oldClassId != null && !oldClassId.equals(newClassId)) {
                // 原班级人数减1
                clazzRepository.findById(oldClassId).ifPresent(oldClazz -> {
                    oldClazz.setStudentCount(Math.max(0, oldClazz.getStudentCount() - 1));
                    clazzRepository.save(oldClazz);
                });
                // 新班级人数加1
                newClazz.setStudentCount(newClazz.getStudentCount() + 1);
                clazzRepository.save(newClazz);
            } else if (oldClassId == null) {
                newClazz.setStudentCount(newClazz.getStudentCount() + 1);
                clazzRepository.save(newClazz);
            }
        }

        student = studentRepository.save(student);
        log.info("更新学生成功: {}", student.getName());
        return toResponse(student);
    }

    /**
     * 删除学生
     */
    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("学生不存在"));
        
        // 更新班级学生人数
        if (student.getClazz() != null) {
            Clazz clazz = student.getClazz();
            clazz.setStudentCount(Math.max(0, clazz.getStudentCount() - 1));
            clazzRepository.save(clazz);
        }

        student.setIsDeleted(true);
        studentRepository.save(student);
        log.info("删除学生成功: {}", student.getName());
    }

    /**
     * 批量删除学生
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 搜索学生
     */
    public List<StudentResponse> search(String keyword) {
        return studentRepository.searchByKeyword(keyword).stream()
                .filter(s -> !s.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建用户账号
     */
    private User createUserAccount(String studentNo, String name, String email) {
        Role studentRole = roleRepository.findByRoleCode("STUDENT").orElse(null);
        
        User user = User.builder()
                .username(studentNo)
                .password(passwordEncoder.encode("123456"))
                .realName(name)
                .email(email)
                .userType(3) // 学生
                .role(studentRole)
                .status(1)
                .build();
        
        return userRepository.save(user);
    }

    /**
     * 复制属性
     */
    private void copyProperties(StudentRequest request, Student student) {
        student.setStudentNo(request.getStudentNo());
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setBirthDate(request.getBirthDate());
        student.setIdCard(request.getIdCard());
        student.setPhone(request.getPhone());
        student.setEmail(request.getEmail());
        student.setAddress(request.getAddress());
        student.setNativePlace(request.getNativePlace());
        student.setNation(request.getNation());
        student.setPoliticalStatus(request.getPoliticalStatus());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setGraduationDate(request.getGraduationDate());
        student.setAvatar(request.getAvatar());
        student.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        student.setRemark(request.getRemark());
    }

    /**
     * 转换为响应对象
     */
    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .studentNo(student.getStudentNo())
                .name(student.getName())
                .gender(student.getGender())
                .birthDate(student.getBirthDate())
                .idCard(student.getIdCard())
                .phone(student.getPhone())
                .email(student.getEmail())
                .address(student.getAddress())
                .nativePlace(student.getNativePlace())
                .nation(student.getNation())
                .politicalStatus(student.getPoliticalStatus())
                .classId(student.getClazz() != null ? student.getClazz().getId() : null)
                .className(student.getClazz() != null ? student.getClazz().getClassName() : null)
                .enrollmentDate(student.getEnrollmentDate())
                .graduationDate(student.getGraduationDate())
                .avatar(student.getAvatar())
                .status(student.getStatus())
                .remark(student.getRemark())
                .userId(student.getUser() != null ? student.getUser().getId() : null)
                .createdAt(student.getCreatedAt())
                .build();
    }
}
