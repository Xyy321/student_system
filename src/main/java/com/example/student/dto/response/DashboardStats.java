package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private Long studentCount;      // 学生总数
    private Long teacherCount;      // 教师总数
    private Long classCount;        // 班级总数
    private Long courseCount;       // 课程总数
    
    private List<ChartData> studentsByClass;    // 各班级学生分布
    private List<ChartData> studentsByGender;   // 学生性别分布
    private List<ChartData> coursesByType;      // 课程类型分布
    private List<ChartData> scoreDistribution;  // 成绩分布

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private String name;
        private Object value;
    }
}
