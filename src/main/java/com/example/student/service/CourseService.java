package com.example.student.service;

import com.example.student.dto.request.CourseRequest;
import com.example.student.dto.response.CourseResponse;
import com.example.student.entity.Course;
import com.example.student.entity.Teacher;
import com.example.student.exception.BusinessException;
import com.example.student.repository.CourseRepository;
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
 * 课程服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    /**
     * 分页查询课程
     */
    public PageVO<CourseResponse> findPage(Integer page, Integer size, String keyword, 
                                           Integer courseType, Long teacherId, String semester) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                Predicate nameLike = cb.like(root.get("courseName"), "%" + keyword + "%");
                Predicate codeLike = cb.like(root.get("courseCode"), "%" + keyword + "%");
                predicates.add(cb.or(nameLike, codeLike));
            }
            if (courseType != null) {
                predicates.add(cb.equal(root.get("courseType"), courseType));
            }
            if (teacherId != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }
            if (StringUtils.hasText(semester)) {
                predicates.add(cb.equal(root.get("semester"), semester));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Course> pageResult = courseRepository.findAll(spec, pageable);
        List<CourseResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有课程
     */
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取可选课程
     */
    public List<CourseResponse> findAvailableCourses() {
        return courseRepository.findAvailableCourses().stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据教师ID获取课程
     */
    public List<CourseResponse> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询课程
     */
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("课程不存在"));
        return toResponse(course);
    }

    /**
     * 根据课程编号查询课程
     */
    public CourseResponse findByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> BusinessException.notFound("课程不存在"));
        return toResponse(course);
    }

    /**
     * 创建课程
     */
    @Transactional
    public CourseResponse create(CourseRequest request) {
        // 检查课程编号是否存在
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw BusinessException.badRequest("课程编号已存在");
        }

        Course course = new Course();
        copyProperties(request, course);

        // 设置授课教师
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> BusinessException.notFound("教师不存在"));
            course.setTeacher(teacher);
        }

        course = courseRepository.save(course);
        log.info("创建课程成功: {}", course.getCourseName());
        return toResponse(course);
    }

    /**
     * 更新课程
     */
    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("课程不存在"));

        // 检查课程编号是否被其他课程使用
        if (!course.getCourseCode().equals(request.getCourseCode()) &&
            courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw BusinessException.badRequest("课程编号已存在");
        }

        copyProperties(request, course);

        // 设置授课教师
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> BusinessException.notFound("教师不存在"));
            course.setTeacher(teacher);
        } else {
            course.setTeacher(null);
        }

        course = courseRepository.save(course);
        log.info("更新课程成功: {}", course.getCourseName());
        return toResponse(course);
    }

    /**
     * 删除课程
     */
    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("课程不存在"));
        course.setIsDeleted(true);
        courseRepository.save(course);
        log.info("删除课程成功: {}", course.getCourseName());
    }

    /**
     * 批量删除课程
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 搜索课程
     */
    public List<CourseResponse> search(String keyword) {
        return courseRepository.searchByKeyword(keyword).stream()
                .filter(c -> !c.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 复制属性
     */
    private void copyProperties(CourseRequest request, Course course) {
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredit(request.getCredit());
        course.setHours(request.getHours());
        course.setCourseType(request.getCourseType());
        course.setDepartment(request.getDepartment());
        course.setSemester(request.getSemester());
        course.setMaxStudents(request.getMaxStudents());
        course.setDescription(request.getDescription());
        course.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    /**
     * 转换为响应对象
     */
    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credit(course.getCredit())
                .hours(course.getHours())
                .courseType(course.getCourseType())
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getName() : null)
                .department(course.getDepartment())
                .semester(course.getSemester())
                .maxStudents(course.getMaxStudents())
                .currentStudents(course.getCurrentStudents())
                .description(course.getDescription())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .build();
    }
}
