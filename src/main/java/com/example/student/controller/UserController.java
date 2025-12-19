package com.example.student.controller;

import com.example.student.dto.request.UserRequest;
import com.example.student.dto.response.UserResponse;
import com.example.student.service.UserService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<PageVO<UserResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        return ResultVO.success(userService.findPage(page, size, keyword, userType, status));
    }

    /**
     * 获取所有用户
     */
    @GetMapping("/all")
    public ResultVO<List<UserResponse>> findAll() {
        return ResultVO.success(userService.findAll());
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public ResultVO<UserResponse> findById(@PathVariable Long id) {
        return ResultVO.success(userService.findById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResultVO.success("创建成功", userService.create(request));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResultVO.success("更新成功", userService.update(id, request));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> batchDelete(@RequestBody List<Long> ids) {
        userService.batchDelete(ids);
        return ResultVO.success("批量删除成功", null);
    }

    /**
     * 更新用户状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return ResultVO.success("状态更新成功", null);
    }

    /**
     * 重置密码
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> resetPassword(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String newPassword = body != null ? body.get("password") : null;
        userService.resetPassword(id, newPassword);
        return ResultVO.success("密码重置成功", null);
    }
}
