package com.example.student.controller;

import com.example.student.dto.response.DashboardStats;
import com.example.student.service.StatisticsService;
import com.example.student.util.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计分析控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public ResultVO<DashboardStats> getDashboardStats() {
        return ResultVO.success(statisticsService.getDashboardStats());
    }

    /**
     * 各班级学生分布
     */
    @GetMapping("/students/by-class")
    public ResultVO<List<DashboardStats.ChartData>> getStudentsByClass() {
        return ResultVO.success(statisticsService.getStudentsByClass());
    }

    /**
     * 学生性别分布
     */
    @GetMapping("/students/by-gender")
    public ResultVO<List<DashboardStats.ChartData>> getStudentsByGender() {
        return ResultVO.success(statisticsService.getStudentsByGender());
    }

    /**
     * 课程类型分布
     */
    @GetMapping("/courses/by-type")
    public ResultVO<List<DashboardStats.ChartData>> getCoursesByType() {
        return ResultVO.success(statisticsService.getCoursesByType());
    }

    /**
     * 课程成绩分布
     */
    @GetMapping("/scores/distribution/{courseId}")
    public ResultVO<List<DashboardStats.ChartData>> getScoreDistribution(@PathVariable Long courseId) {
        return ResultVO.success(statisticsService.getScoreDistribution(courseId));
    }

    /**
     * 教师院系分布
     */
    @GetMapping("/teachers/by-department")
    public ResultVO<List<DashboardStats.ChartData>> getTeachersByDepartment() {
        return ResultVO.success(statisticsService.getTeachersByDepartment());
    }

    /**
     * 学生成绩统计
     */
    @GetMapping("/scores/student/{studentId}")
    public ResultVO<List<DashboardStats.ChartData>> getStudentScores(@PathVariable Long studentId) {
        return ResultVO.success(statisticsService.getStudentScores(studentId));
    }
}
