package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String studentNo;
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
    private String className;
    private LocalDate enrollmentDate;
    private LocalDate graduationDate;
    private String avatar;
    private Integer status;
    private String remark;
    private Long userId;
    private LocalDateTime createdAt;
}
