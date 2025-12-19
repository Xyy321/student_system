package com.example.student.service;

import com.example.student.dto.response.DashboardStats;
import com.example.student.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClazzRepository clazzRepository;
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;

    /**
     * 获取仪表盘统计数据
     */
    public DashboardStats getDashboardStats() {
        return DashboardStats.builder()
                .studentCount(studentRepository.countActiveStudents())
                .teacherCount(teacherRepository.countActiveTeachers())
                .classCount(clazzRepository.countAllClasses())
                .courseCount(courseRepository.countAllCourses())
                .studentsByClass(getStudentsByClass())
                .studentsByGender(getStudentsByGender())
                .coursesByType(getCoursesByType())
                .build();
    }

    /**
     * 各班级学生分布
     */
    public List<DashboardStats.ChartData> getStudentsByClass() {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = studentRepository.countStudentsByClass();
        for (Object[] row : data) {
            String className = row[0] != null ? row[0].toString() : "未分班";
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            result.add(DashboardStats.ChartData.builder()
                    .name(className)
                    .value(count)
                    .build());
        }
        return result;
    }

    /**
     * 学生性别分布
     */
    public List<DashboardStats.ChartData> getStudentsByGender() {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = studentRepository.countStudentsByGender();
        for (Object[] row : data) {
            Integer gender = row[0] != null ? ((Number) row[0]).intValue() : 0;
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            String genderName = switch (gender) {
                case 1 -> "男";
                case 2 -> "女";
                default -> "未知";
            };
            result.add(DashboardStats.ChartData.builder()
                    .name(genderName)
                    .value(count)
                    .build());
        }
        return result;
    }

    /**
     * 课程类型分布
     */
    public List<DashboardStats.ChartData> getCoursesByType() {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = courseRepository.countCoursesByType();
        for (Object[] row : data) {
            Integer type = row[0] != null ? ((Number) row[0]).intValue() : 0;
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            String typeName = switch (type) {
                case 1 -> "必修课";
                case 2 -> "选修课";
                case 3 -> "公选课";
                default -> "其他";
            };
            result.add(DashboardStats.ChartData.builder()
                    .name(typeName)
                    .value(count)
                    .build());
        }
        return result;
    }

    /**
     * 获取课程成绩分布
     */
    public List<DashboardStats.ChartData> getScoreDistribution(Long courseId) {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = scoreRepository.getScoreDistribution(courseId);
        if (!data.isEmpty()) {
            Object[] row = data.get(0);
            String[] labels = {"优秀(90-100)", "良好(80-89)", "中等(70-79)", "及格(60-69)", "不及格(<60)"};
            for (int i = 0; i < 5; i++) {
                Long count = row[i] != null ? ((Number) row[i]).longValue() : 0L;
                result.add(DashboardStats.ChartData.builder()
                        .name(labels[i])
                        .value(count)
                        .build());
            }
        }
        return result;
    }

    /**
     * 获取教师院系分布
     */
    public List<DashboardStats.ChartData> getTeachersByDepartment() {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = teacherRepository.countTeachersByDepartment();
        for (Object[] row : data) {
            String department = row[0] != null ? row[0].toString() : "未知";
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            result.add(DashboardStats.ChartData.builder()
                    .name(department)
                    .value(count)
                    .build());
        }
        return result;
    }

    /**
     * 获取学生成绩统计
     */
    public List<DashboardStats.ChartData> getStudentScores(Long studentId) {
        List<DashboardStats.ChartData> result = new ArrayList<>();
        List<Object[]> data = scoreRepository.getStudentScoresByCourse(studentId);
        for (Object[] row : data) {
            String courseName = row[0] != null ? row[0].toString() : "未知";
            Double score = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            result.add(DashboardStats.ChartData.builder()
                    .name(courseName)
                    .value(score)
                    .build());
        }
        return result;
    }
}
