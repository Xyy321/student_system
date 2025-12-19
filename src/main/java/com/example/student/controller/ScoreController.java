package com.example.student.controller;

import com.example.student.dto.request.ScoreRequest;
import com.example.student.dto.response.ScoreResponse;
import com.example.student.service.ScoreService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制器
 */
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 分页查询成绩
     */
    @GetMapping
    public ResultVO<PageVO<ScoreResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {
        return ResultVO.success(scoreService.findPage(page, size, studentId, courseId, semester));
    }

    /**
     * 根据学生ID获取成绩列表
     */
    @GetMapping("/student/{studentId}")
    public ResultVO<List<ScoreResponse>> findByStudentId(@PathVariable Long studentId) {
        return ResultVO.success(scoreService.findByStudentId(studentId));
    }

    /**
     * 根据课程ID获取成绩列表
     */
    @GetMapping("/course/{courseId}")
    public ResultVO<List<ScoreResponse>> findByCourseId(@PathVariable Long courseId) {
        return ResultVO.success(scoreService.findByCourseId(courseId));
    }

    /**
     * 根据ID查询成绩
     */
    @GetMapping("/{id}")
    public ResultVO<ScoreResponse> findById(@PathVariable Long id) {
        return ResultVO.success(scoreService.findById(id));
    }

    /**
     * 学生选课
     */
    @PostMapping("/select")
    public ResultVO<ScoreResponse> selectCourse(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");
        return ResultVO.success("选课成功", scoreService.selectCourse(studentId, courseId));
    }

    /**
     * 退选课程
     */
    @PostMapping("/drop")
    public ResultVO<Void> dropCourse(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");
        scoreService.dropCourse(studentId, courseId);
        return ResultVO.success("退选成功", null);
    }

    /**
     * 录入成绩
     */
    @PostMapping("/{id}/input")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<ScoreResponse> inputScore(@PathVariable Long id, @RequestBody ScoreRequest request) {
        return ResultVO.success("成绩录入成功", scoreService.inputScore(id, request));
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/batch-input")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> batchInputScore(@RequestBody List<ScoreRequest> requests) {
        scoreService.batchInputScore(requests);
        return ResultVO.success("批量录入成功", null);
    }

    /**
     * 确认成绩
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<ScoreResponse> confirmScore(@PathVariable Long id) {
        return ResultVO.success("成绩确认成功", scoreService.confirmScore(id));
    }

    /**
     * 删除成绩记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        scoreService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 获取学生平均成绩
     */
    @GetMapping("/student/{studentId}/average")
    public ResultVO<BigDecimal> getStudentAverageScore(@PathVariable Long studentId) {
        return ResultVO.success(scoreService.getStudentAverageScore(studentId));
    }

    /**
     * 获取学生平均绩点
     */
    @GetMapping("/student/{studentId}/gpa")
    public ResultVO<BigDecimal> getStudentAverageGpa(@PathVariable Long studentId) {
        return ResultVO.success(scoreService.getStudentAverageGpa(studentId));
    }

    /**
     * 获取课程平均分
     */
    @GetMapping("/course/{courseId}/average")
    public ResultVO<BigDecimal> getCourseAverageScore(@PathVariable Long courseId) {
        return ResultVO.success(scoreService.getCourseAverageScore(courseId));
    }
}
