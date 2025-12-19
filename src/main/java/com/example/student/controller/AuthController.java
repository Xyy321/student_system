package com.example.student.controller;

import com.example.student.dto.request.ChangePasswordRequest;
import com.example.student.dto.request.LoginRequest;
import com.example.student.dto.request.RegisterRequest;
import com.example.student.dto.response.LoginResponse;
import com.example.student.security.CustomUserDetails;
import com.example.student.service.AuthService;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultVO<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResultVO.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResultVO<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResultVO.success("注册成功", null);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResultVO<Void> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUserId(), request);
        return ResultVO.success("密码修改成功", null);
    }

    /**
     * 忘记密码 - 发送验证码
     */
    @PostMapping("/forgot-password")
    public ResultVO<Void> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.forgotPassword(email);
        return ResultVO.success("验证码已发送到您的邮箱", null);
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResultVO<Void> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verifyCode = request.get("verifyCode");
        String newPassword = request.get("newPassword");
        authService.resetPassword(email, verifyCode, newPassword);
        return ResultVO.success("密码重置成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResultVO<LoginResponse.UserInfo> getCurrentUser() {
        return ResultVO.success(authService.getCurrentUserInfo());
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResultVO<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        return ResultVO.success("退出成功", null);
    }
}
