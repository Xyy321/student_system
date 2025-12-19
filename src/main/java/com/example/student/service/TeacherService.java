package com.example.student.service;

import com.example.student.dto.request.TeacherRequest;
import com.example.student.dto.response.TeacherResponse;
import com.example.student.entity.Role;
import com.example.student.entity.Teacher;
import com.example.student.entity.User;
import com.example.student.exception.BusinessException;
import com.example.student.repository.RoleRepository;
import com.example.student.repository.TeacherRepository;
import com.example.student.repository.UserRepository;
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
 * 教师服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询教师
     */
    public PageVO<TeacherResponse> findPage(Integer page, Integer size, String keyword, 
                                            String department, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Teacher> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                Predicate nameLike = cb.like(root.get("name"), "%" + keyword + "%");
                Predicate teacherNoLike = cb.like(root.get("teacherNo"), "%" + keyword + "%");
                predicates.add(cb.or(nameLike, teacherNoLike));
            }
            if (StringUtils.hasText(department)) {
                predicates.add(cb.equal(root.get("department"), department));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Teacher> pageResult = teacherRepository.findAll(spec, pageable);
        List<TeacherResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有教师
     */
    public List<TeacherResponse> findAll() {
        return teacherRepository.findAll().stream()
                .filter(t -> !t.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询教师
     */
    public TeacherResponse findById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("教师不存在"));
        return toResponse(teacher);
    }

    /**
     * 根据工号查询教师
     */
    public TeacherResponse findByTeacherNo(String teacherNo) {
        Teacher teacher = teacherRepository.findByTeacherNo(teacherNo)
                .orElseThrow(() -> BusinessException.notFound("教师不存在"));
        return toResponse(teacher);
    }

    /**
     * 创建教师
     */
    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        // 检查工号是否存在
        if (teacherRepository.existsByTeacherNo(request.getTeacherNo())) {
            throw BusinessException.badRequest("工号已存在");
        }

        Teacher teacher = new Teacher();
        copyProperties(request, teacher);

        // 自动创建登录账号
        if (request.getCreateAccount() != null && request.getCreateAccount()) {
            User user = createUserAccount(request.getTeacherNo(), request.getName(), request.getEmail());
            teacher.setUser(user);
        }

        teacher = teacherRepository.save(teacher);
        log.info("创建教师成功: {}", teacher.getName());
        return toResponse(teacher);
    }

    /**
     * 更新教师
     */
    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("教师不存在"));

        // 检查工号是否被其他教师使用
        if (!teacher.getTeacherNo().equals(request.getTeacherNo()) &&
            teacherRepository.existsByTeacherNo(request.getTeacherNo())) {
            throw BusinessException.badRequest("工号已存在");
        }

        copyProperties(request, teacher);
        teacher = teacherRepository.save(teacher);
        log.info("更新教师成功: {}", teacher.getName());
        return toResponse(teacher);
    }

    /**
     * 删除教师
     */
    @Transactional
    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("教师不存在"));
        teacher.setIsDeleted(true);
        teacherRepository.save(teacher);
        log.info("删除教师成功: {}", teacher.getName());
    }

    /**
     * 批量删除教师
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 搜索教师
     */
    public List<TeacherResponse> search(String keyword) {
        return teacherRepository.searchByKeyword(keyword).stream()
                .filter(t -> !t.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 创建用户账号
     */
    private User createUserAccount(String teacherNo, String name, String email) {
        Role teacherRole = roleRepository.findByRoleCode("TEACHER").orElse(null);
        
        User user = User.builder()
                .username(teacherNo)
                .password(passwordEncoder.encode("123456"))
                .realName(name)
                .email(email)
                .userType(2) // 教师
                .role(teacherRole)
                .status(1)
                .build();
        
        return userRepository.save(user);
    }

    /**
     * 复制属性
     */
    private void copyProperties(TeacherRequest request, Teacher teacher) {
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setName(request.getName());
        teacher.setGender(request.getGender());
        teacher.setBirthDate(request.getBirthDate());
        teacher.setPhone(request.getPhone());
        teacher.setEmail(request.getEmail());
        teacher.setDepartment(request.getDepartment());
        teacher.setTitle(request.getTitle());
        teacher.setEducation(request.getEducation());
        teacher.setEntryDate(request.getEntryDate());
        teacher.setAddress(request.getAddress());
        teacher.setAvatar(request.getAvatar());
        teacher.setIntroduction(request.getIntroduction());
        teacher.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    /**
     * 转换为响应对象
     */
    private TeacherResponse toResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .teacherNo(teacher.getTeacherNo())
                .name(teacher.getName())
                .gender(teacher.getGender())
                .birthDate(teacher.getBirthDate())
                .phone(teacher.getPhone())
                .email(teacher.getEmail())
                .department(teacher.getDepartment())
                .title(teacher.getTitle())
                .education(teacher.getEducation())
                .entryDate(teacher.getEntryDate())
                .address(teacher.getAddress())
                .avatar(teacher.getAvatar())
                .introduction(teacher.getIntroduction())
                .status(teacher.getStatus())
                .userId(teacher.getUser() != null ? teacher.getUser().getId() : null)
                .createdAt(teacher.getCreatedAt())
                .build();
    }
}
