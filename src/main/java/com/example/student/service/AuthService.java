package com.example.student.service;

import com.example.student.dto.request.ChangePasswordRequest;
import com.example.student.dto.request.LoginRequest;
import com.example.student.dto.request.RegisterRequest;
import com.example.student.dto.response.LoginResponse;
import com.example.student.entity.Role;
import com.example.student.entity.User;
import com.example.student.exception.BusinessException;
import com.example.student.repository.RoleRepository;
import com.example.student.repository.UserRepository;
import com.example.student.security.CustomUserDetails;
import com.example.student.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 生成令牌
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

        // 更新登录信息
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow();
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 构建响应
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .userInfo(buildUserInfo(user))
                .build();
    }

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 校验密码
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.badRequest("两次密码输入不一致");
        }

        // 检查用户名是否存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.badRequest("用户名已存在");
        }

        // 检查邮箱是否存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.badRequest("邮箱已被注册");
        }

        // 获取默认角色
        String roleCode = switch (request.getUserType()) {
            case 1 -> "ADMIN";
            case 2 -> "TEACHER";
            default -> "STUDENT";
        };
        Role role = roleRepository.findByRoleCode(roleCode).orElse(null);

        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .realName(request.getRealName())
                .phone(request.getPhone())
                .userType(request.getUserType())
                .role(role)
                .status(1)
                .build();

        userRepository.save(user);
        log.info("用户注册成功: {}", request.getUsername());
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.badRequest("两次密码输入不一致");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("用户修改密码成功: {}", user.getUsername());
    }

    /**
     * 忘记密码 - 发送验证码
     */
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("该邮箱未注册"));

        // 生成验证码
        String verifyCode = String.format("%06d", new Random().nextInt(1000000));
        user.setVerifyCode(verifyCode);
        user.setVerifyCodeExpireTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // TODO: 发送邮件（实际项目中应该发送邮件）
        log.info("验证码已发送到邮箱 {}: {}", email, verifyCode);
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(String email, String verifyCode, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BusinessException.notFound("该邮箱未注册"));

        // 验证验证码
        if (user.getVerifyCode() == null || !user.getVerifyCode().equals(verifyCode)) {
            throw BusinessException.badRequest("验证码错误");
        }

        if (user.getVerifyCodeExpireTime() == null || 
            user.getVerifyCodeExpireTime().isBefore(LocalDateTime.now())) {
            throw BusinessException.badRequest("验证码已过期");
        }

        // 重置密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerifyCode(null);
        user.setVerifyCodeExpireTime(null);
        userRepository.save(user);
        log.info("用户重置密码成功: {}", user.getUsername());
    }

    /**
     * 获取当前用户信息
     */
    public LoginResponse.UserInfo getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.unauthorized("请先登录");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        return buildUserInfo(user);
    }

    /**
     * 构建用户信息
     */
    private LoginResponse.UserInfo buildUserInfo(User user) {
        return LoginResponse.UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .roleCode(user.getRole() != null ? user.getRole().getRoleCode() : null)
                .build();
    }
}
