package com.example.student.service;

import com.example.student.dto.request.UserRequest;
import com.example.student.dto.response.UserResponse;
import com.example.student.entity.Role;
import com.example.student.entity.User;
import com.example.student.exception.BusinessException;
import com.example.student.repository.RoleRepository;
import com.example.student.repository.UserRepository;
import com.example.student.util.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    public PageVO<UserResponse> findPage(Integer page, Integer size, String keyword, Integer userType, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                Predicate usernameLike = cb.like(root.get("username"), "%" + keyword + "%");
                Predicate realNameLike = cb.like(root.get("realName"), "%" + keyword + "%");
                Predicate emailLike = cb.like(root.get("email"), "%" + keyword + "%");
                predicates.add(cb.or(usernameLike, realNameLike, emailLike));
            }
            if (userType != null) {
                predicates.add(cb.equal(root.get("userType"), userType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<User> pageResult = userRepository.findAll(spec, pageable);
        List<UserResponse> records = pageResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有用户
     */
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .filter(u -> !u.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询用户
     */
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        return toResponse(user);
    }

    /**
     * 创建用户
     */
    @Transactional
    public UserResponse create(UserRequest request) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.badRequest("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.badRequest("邮箱已被使用");
        }
        
        User user = new User();
        copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "123456"));
        
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId()).orElse(null);
            user.setRole(role);
        }
        
        user = userRepository.save(user);
        log.info("创建用户成功: {}", user.getUsername());
        return toResponse(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.badRequest("用户名已存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && 
            !request.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.badRequest("邮箱已被使用");
        }
        
        copyProperties(request, user);
        
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId()).orElse(null);
            user.setRole(role);
        }
        
        // 如果传入了新密码，则更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        log.info("更新用户成功: {}", user.getUsername());
        return toResponse(user);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("删除用户成功: {}", user.getUsername());
    }

    /**
     * 批量删除用户
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
        log.info("更新用户状态: {} -> {}", user.getUsername(), status);
    }

    /**
     * 重置密码
     */
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword != null ? newPassword : "123456"));
        userRepository.save(user);
        log.info("重置用户密码: {}", user.getUsername());
    }

    /**
     * 复制属性
     */
    private void copyProperties(UserRequest request, User user) {
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setGender(request.getGender());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setUserType(request.getUserType() != null ? request.getUserType() : 3);
    }

    /**
     * 转换为响应对象
     */
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .status(user.getStatus())
                .userType(user.getUserType())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .lastLoginTime(user.getLastLoginTime())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
