package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色请求DTO
 */
@Data
public class RoleRequest {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    private String description;

    private Integer status;

    private List<Long> menuIds;
}
