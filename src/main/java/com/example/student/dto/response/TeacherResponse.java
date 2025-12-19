package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {

    private Long id;
    private String teacherNo;
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
    private Long userId;
    private LocalDateTime createdAt;
}
