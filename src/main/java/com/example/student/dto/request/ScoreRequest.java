package com.example.student.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 成绩请求DTO
 */
@Data
public class ScoreRequest {

    private Long id;

    private Long studentId;

    private Long courseId;

    private BigDecimal usualScore;    // 平时成绩

    private BigDecimal midtermScore;  // 期中成绩

    private BigDecimal finalScore;    // 期末成绩

    private String semester;

    private String remark;
}
