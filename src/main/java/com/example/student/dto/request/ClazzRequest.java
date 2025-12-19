package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 班级请求DTO
 */
@Data
public class ClazzRequest {

    @NotBlank(message = "班级名称不能为空")
    private String className;

    @NotBlank(message = "班级编号不能为空")
    private String classCode;

    private String grade;

    private String major;

    private String department;

    private Long headTeacherId;

    private String description;

    private Integer status;
}
