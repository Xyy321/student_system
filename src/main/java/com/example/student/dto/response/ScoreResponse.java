package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponse {

    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private BigDecimal usualScore;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;
    private BigDecimal gpa;
    private String semester;
    private Integer status;  // 0: 未录入, 1: 已录入, 2: 已确认
    private String remark;
    private LocalDateTime createdAt;
}
