package com.example.student.controller;

import com.example.student.dto.request.MenuRequest;
import com.example.student.dto.response.MenuResponse;
import com.example.student.service.MenuService;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取当前用户的菜单
     */
    @GetMapping("/current")
    public ResultVO<List<MenuResponse>> getCurrentUserMenus() {
        return ResultVO.success(menuService.getCurrentUserMenus());
    }

    /**
     * 获取所有菜单（树形结构）
     */
    @GetMapping("/tree")
    public ResultVO<List<MenuResponse>> findAllTree() {
        return ResultVO.success(menuService.findAllTree());
    }

    /**
     * 获取所有菜单（平铺）
     */
    @GetMapping("/all")
    public ResultVO<List<MenuResponse>> findAll() {
        return ResultVO.success(menuService.findAll());
    }

    /**
     * 根据ID查询菜单
     */
    @GetMapping("/{id}")
    public ResultVO<MenuResponse> findById(@PathVariable Long id) {
        return ResultVO.success(menuService.findById(id));
    }

    /**
     * 创建菜单
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<MenuResponse> create(@Valid @RequestBody MenuRequest request) {
        return ResultVO.success("创建成功", menuService.create(request));
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<MenuResponse> update(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        return ResultVO.success("更新成功", menuService.update(id, request));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResultVO.success("删除成功", null);
    }
}
