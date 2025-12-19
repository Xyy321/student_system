package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师请求DTO
 */
@Data
public class TeacherRequest {

    @NotBlank(message = "工号不能为空")
    private String teacherNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private Integer gender;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String department;

    private String title;

    private String education;

    private LocalDate entryDate;

    private String address;

    private String avatar;

    private String introduction;

    private Integer status;

    private Boolean createAccount; // 是否自动创建登录账号
}
