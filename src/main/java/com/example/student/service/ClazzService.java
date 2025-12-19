package com.example.student.service;

import com.example.student.dto.request.ClazzRequest;
import com.example.student.dto.response.ClazzResponse;
import com.example.student.entity.Clazz;
import com.example.student.entity.Teacher;
import com.example.student.exception.BusinessException;
import com.example.student.repository.ClazzRepository;
import com.example.student.repository.TeacherRepository;
import com.example.student.util.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClazzService {

    private final ClazzRepository clazzRepository;
    private final TeacherRepository teacherRepository;

    /**
     * 分页查询班级
     */
    public PageVO<ClazzResponse> findPage(Integer page, Integer size, String keyword, 
                                          String grade, String department) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Clazz> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                Predicate nameLike = cb.like(root.get("className"), "%" + keyword + "%");
                Predicate codeLike = cb.like(root.get("classCode"), "%" + keyword + "%");
                predicates.add(cb.or(nameLike, codeLike));
            }
            if (StringUtils.hasText(grade)) {
                predicates.add(cb.equal(root.get("grade"), grade));
            }
            if (StringUtils.hasText(department)) {
                predicates.add(cb.equal(root.get("department"), department));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Clazz> pageResult = clazzRepository.findAll(spec, pageable);
        List<ClazzResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有班级
     */
    public List<ClazzResponse> findAll() {
        return clazzRepository.findAllActiveClasses().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询班级
     */
    public ClazzResponse findById(Long id) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("班级不存在"));
        return toResponse(clazz);
    }

    /**
     * 根据班级编号查询班级
     */
    public ClazzResponse findByClassCode(String classCode) {
        Clazz clazz = clazzRepository.findByClassCode(classCode)
                .orElseThrow(() -> BusinessException.notFound("班级不存在"));
        return toResponse(clazz);
    }

    /**
     * 创建班级
     */
    @Transactional
    public ClazzResponse create(ClazzRequest request) {
        // 检查班级编号是否存在
        if (clazzRepository.existsByClassCode(request.getClassCode())) {
            throw BusinessException.badRequest("班级编号已存在");
        }

        Clazz clazz = new Clazz();
        copyProperties(request, clazz);

        // 设置班主任
        if (request.getHeadTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getHeadTeacherId())
                    .orElseThrow(() -> BusinessException.notFound("教师不存在"));
            clazz.setHeadTeacher(teacher);
        }

        clazz = clazzRepository.save(clazz);
        log.info("创建班级成功: {}", clazz.getClassName());
        return toResponse(clazz);
    }

    /**
     * 更新班级
     */
    @Transactional
    public ClazzResponse update(Long id, ClazzRequest request) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("班级不存在"));

        // 检查班级编号是否被其他班级使用
        if (!clazz.getClassCode().equals(request.getClassCode()) &&
            clazzRepository.existsByClassCode(request.getClassCode())) {
            throw BusinessException.badRequest("班级编号已存在");
        }

        copyProperties(request, clazz);

        // 设置班主任
        if (request.getHeadTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getHeadTeacherId())
                    .orElseThrow(() -> BusinessException.notFound("教师不存在"));
            clazz.setHeadTeacher(teacher);
        } else {
            clazz.setHeadTeacher(null);
        }

        clazz = clazzRepository.save(clazz);
        log.info("更新班级成功: {}", clazz.getClassName());
        return toResponse(clazz);
    }

    /**
     * 删除班级
     */
    @Transactional
    public void delete(Long id) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("班级不存在"));
        
        // 检查是否有学生
        if (clazz.getStudentCount() > 0) {
            throw BusinessException.badRequest("班级中还有学生，无法删除");
        }

        clazz.setIsDeleted(true);
        clazzRepository.save(clazz);
        log.info("删除班级成功: {}", clazz.getClassName());
    }

    /**
     * 批量删除班级
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 根据年级获取班级列表
     */
    public List<ClazzResponse> findByGrade(String grade) {
        return clazzRepository.findByGrade(grade).stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 复制属性
     */
    private void copyProperties(ClazzRequest request, Clazz clazz) {
        clazz.setClassName(request.getClassName());
        clazz.setClassCode(request.getClassCode());
        clazz.setGrade(request.getGrade());
        clazz.setMajor(request.getMajor());
        clazz.setDepartment(request.getDepartment());
        clazz.setDescription(request.getDescription());
        clazz.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    /**
     * 转换为响应对象
     */
    private ClazzResponse toResponse(Clazz clazz) {
        return ClazzResponse.builder()
                .id(clazz.getId())
                .className(clazz.getClassName())
                .classCode(clazz.getClassCode())
                .grade(clazz.getGrade())
                .major(clazz.getMajor())
                .department(clazz.getDepartment())
                .headTeacherId(clazz.getHeadTeacher() != null ? clazz.getHeadTeacher().getId() : null)
                .headTeacherName(clazz.getHeadTeacher() != null ? clazz.getHeadTeacher().getName() : null)
                .studentCount(clazz.getStudentCount())
                .description(clazz.getDescription())
                .status(clazz.getStatus())
                .createdAt(clazz.getCreatedAt())
                .build();
    }
}
