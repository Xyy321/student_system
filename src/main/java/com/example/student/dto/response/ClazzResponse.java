package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 班级响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClazzResponse {

    private Long id;
    private String className;
    private String classCode;
    private String grade;
    private String major;
    private String department;
    private Long headTeacherId;
    private String headTeacherName;
    private Integer studentCount;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
}
