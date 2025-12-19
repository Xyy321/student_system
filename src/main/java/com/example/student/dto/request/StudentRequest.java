package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学生请求DTO
 */
@Data
public class StudentRequest {

    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private Integer gender;

    private LocalDate birthDate;

    private String idCard;

    private String phone;

    private String email;

    private String address;

    private String nativePlace;

    private String nation;

    private String politicalStatus;

    private Long classId;

    private LocalDate enrollmentDate;

    private LocalDate graduationDate;

    private String avatar;

    private Integer status;

    private String remark;

    private Boolean createAccount; // 是否自动创建登录账号
}
