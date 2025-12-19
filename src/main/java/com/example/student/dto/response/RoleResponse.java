package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
}
