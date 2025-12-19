package com.example.student.controller;

import com.example.student.dto.request.RoleRequest;
import com.example.student.dto.response.RoleResponse;
import com.example.student.service.RoleService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<PageVO<RoleResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResultVO.success(roleService.findPage(page, size));
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    public ResultVO<List<RoleResponse>> findAll() {
        return ResultVO.success(roleService.findAll());
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public ResultVO<RoleResponse> findById(@PathVariable Long id) {
        return ResultVO.success(roleService.findById(id));
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        return ResultVO.success("创建成功", roleService.create(request));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ResultVO.success("更新成功", roleService.update(id, request));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/{id}/menus")
    public ResultVO<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return ResultVO.success(roleService.getRoleMenuIds(id));
    }
}
