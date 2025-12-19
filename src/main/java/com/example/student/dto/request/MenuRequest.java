package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 菜单请求DTO
 */
@Data
public class MenuRequest {

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Integer menuType;

    private String permission;

    private Boolean visible;

    private Integer status;
}
