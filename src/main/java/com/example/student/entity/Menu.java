package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单实体 - 支持二级菜单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_menu")
public class Menu extends BaseEntity {

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "parent_id")
    private Long parentId = 0L; // 0表示顶级菜单

    @Column(name = "path", length = 200)
    private String path; // 路由路径

    @Column(name = "component", length = 200)
    private String component; // 前端组件路径

    @Column(name = "icon", length = 100)
    private String icon; // 菜单图标

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    @Column(name = "menu_type")
    private Integer menuType = 1; // 1: 目录, 2: 菜单, 3: 按钮

    @Column(name = "permission", length = 100)
    private String permission; // 权限标识

    @Column(name = "visible")
    private Boolean visible = true; // 是否可见

    @Column(name = "status")
    private Integer status = 1; // 1: 启用, 0: 禁用

    @Transient
    private List<Menu> children = new ArrayList<>();
}
