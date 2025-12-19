package com.example.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private Long id;
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
    private LocalDateTime createdAt;
    private List<MenuResponse> children;
}
