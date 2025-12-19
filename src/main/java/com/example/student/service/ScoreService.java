package com.example.student.service;

import com.example.student.dto.request.ScoreRequest;
import com.example.student.dto.response.ScoreResponse;
import com.example.student.entity.Course;
import com.example.student.entity.Score;
import com.example.student.entity.Student;
import com.example.student.exception.BusinessException;
import com.example.student.repository.CourseRepository;
import com.example.student.repository.ScoreRepository;
import com.example.student.repository.StudentRepository;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成绩服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * 分页查询成绩
     */
    public PageVO<ScoreResponse> findPage(Integer page, Integer size, Long studentId, 
                                          Long courseId, String semester) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Score> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (studentId != null) {
                predicates.add(cb.equal(root.get("student").get("id"), studentId));
            }
            if (courseId != null) {
                predicates.add(cb.equal(root.get("course").get("id"), courseId));
            }
            if (StringUtils.hasText(semester)) {
                predicates.add(cb.equal(root.get("semester"), semester));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Score> pageResult = scoreRepository.findAll(spec, pageable);
        List<ScoreResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 根据学生ID获取成绩列表
     */
    public List<ScoreResponse> findByStudentId(Long studentId) {
        return scoreRepository.findByStudentId(studentId).stream()
                .filter(s -> !s.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据课程ID获取成绩列表
     */
    public List<ScoreResponse> findByCourseId(Long courseId) {
        return scoreRepository.findByCourseId(courseId).stream()
                .filter(s -> !s.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询成绩
     */
    public ScoreResponse findById(Long id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("成绩不存在"));
        return toResponse(score);
    }

    /**
     * 学生选课
     */
    @Transactional
    public ScoreResponse selectCourse(Long studentId, Long courseId) {
        // 检查是否已选
        if (scoreRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw BusinessException.badRequest("已选择该课程");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> BusinessException.notFound("学生不存在"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("课程不存在"));

        // 检查课程是否已满
        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw BusinessException.badRequest("课程已满员");
        }

        Score score = Score.builder()
                .student(student)
                .course(course)
                .semester(course.getSemester())
                .status(0) // 未录入成绩
                .build();

        score = scoreRepository.save(score);

        // 更新选课人数
        course.setCurrentStudents(course.getCurrentStudents() + 1);
        courseRepository.save(course);

        log.info("学生选课成功: {} -> {}", student.getName(), course.getCourseName());
        return toResponse(score);
    }

    /**
     * 退选课程
     */
    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        Score score = scoreRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> BusinessException.notFound("未选择该课程"));

        // 如果已有成绩，不允许退选
        if (score.getStatus() == 2) {
            throw BusinessException.badRequest("成绩已确认，无法退选");
        }

        Course course = score.getCourse();
        course.setCurrentStudents(Math.max(0, course.getCurrentStudents() - 1));
        courseRepository.save(course);

        scoreRepository.delete(score);
        log.info("学生退选成功: {} -> {}", score.getStudent().getName(), course.getCourseName());
    }

    /**
     * 录入成绩
     */
    @Transactional
    public ScoreResponse inputScore(Long id, ScoreRequest request) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("成绩记录不存在"));

        score.setUsualScore(request.getUsualScore());
        score.setMidtermScore(request.getMidtermScore());
        score.setFinalScore(request.getFinalScore());
        score.setRemark(request.getRemark());

        // 计算总成绩和绩点
        score.calculateTotalScore();
        score.setStatus(1); // 已录入

        score = scoreRepository.save(score);
        log.info("成绩录入成功: {} - {} = {}", score.getStudent().getName(), 
                score.getCourse().getCourseName(), score.getTotalScore());
        return toResponse(score);
    }

    /**
     * 批量录入成绩
     */
    @Transactional
    public void batchInputScore(List<ScoreRequest> requests) {
        for (ScoreRequest request : requests) {
            if (request.getId() != null) {
                inputScore(request.getId(), request);
            }
        }
    }

    /**
     * 确认成绩
     */
    @Transactional
    public ScoreResponse confirmScore(Long id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("成绩记录不存在"));

        if (score.getTotalScore() == null) {
            throw BusinessException.badRequest("请先录入成绩");
        }

        score.setStatus(2); // 已确认
        score = scoreRepository.save(score);
        log.info("成绩确认成功: {} - {}", score.getStudent().getName(), score.getCourse().getCourseName());
        return toResponse(score);
    }

    /**
     * 删除成绩记录
     */
    @Transactional
    public void delete(Long id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("成绩不存在"));

        if (score.getStatus() == 2) {
            throw BusinessException.badRequest("成绩已确认，无法删除");
        }

        // 更新选课人数
        Course course = score.getCourse();
        course.setCurrentStudents(Math.max(0, course.getCurrentStudents() - 1));
        courseRepository.save(course);

        scoreRepository.delete(score);
        log.info("成绩删除成功");
    }

    /**
     * 获取学生平均成绩
     */
    public BigDecimal getStudentAverageScore(Long studentId) {
        return scoreRepository.calculateAverageScore(studentId);
    }

    /**
     * 获取学生平均绩点
     */
    public BigDecimal getStudentAverageGpa(Long studentId) {
        return scoreRepository.calculateAverageGpa(studentId);
    }

    /**
     * 获取课程平均分
     */
    public BigDecimal getCourseAverageScore(Long courseId) {
        return scoreRepository.calculateCourseAverageScore(courseId);
    }

    /**
     * 转换为响应对象
     */
    private ScoreResponse toResponse(Score score) {
        return ScoreResponse.builder()
                .id(score.getId())
                .studentId(score.getStudent().getId())
                .studentNo(score.getStudent().getStudentNo())
                .studentName(score.getStudent().getName())
                .courseId(score.getCourse().getId())
                .courseCode(score.getCourse().getCourseCode())
                .courseName(score.getCourse().getCourseName())
                .credit(score.getCourse().getCredit())
                .usualScore(score.getUsualScore())
                .midtermScore(score.getMidtermScore())
                .finalScore(score.getFinalScore())
                .totalScore(score.getTotalScore())
                .gpa(score.getGpa())
                .semester(score.getSemester())
                .status(score.getStatus())
                .remark(score.getRemark())
                .createdAt(score.getCreatedAt())
                .build();
    }
}
