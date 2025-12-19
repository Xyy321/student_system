package com.example.student.service;

import com.example.student.dto.request.RoleRequest;
import com.example.student.dto.response.RoleResponse;
import com.example.student.entity.Menu;
import com.example.student.entity.Role;
import com.example.student.exception.BusinessException;
import com.example.student.repository.MenuRepository;
import com.example.student.repository.RoleRepository;
import com.example.student.util.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;

    /**
     * 分页查询角色
     */
    public PageVO<RoleResponse> findPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Role> pageResult = roleRepository.findAll(pageable);
        List<RoleResponse> records = pageResult.getContent().stream()
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageVO.fromPage(pageResult, records);
    }

    /**
     * 获取所有角色
     */
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .filter(r -> !r.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询角色
     */
    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("角色不存在"));
        return toResponse(role);
    }

    /**
     * 创建角色
     */
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw BusinessException.badRequest("角色编码已存在");
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // 设置菜单权限
        if (request.getMenuIds() != null && !request.getMenuIds().isEmpty()) {
            Set<Menu> menus = new HashSet<>(menuRepository.findAllById(request.getMenuIds()));
            role.setMenus(menus);
        }

        role = roleRepository.save(role);
        log.info("创建角色成功: {}", role.getRoleName());
        return toResponse(role);
    }

    /**
     * 更新角色
     */
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("角色不存在"));

        // 检查角色编码是否被其他角色使用
        if (!role.getRoleCode().equals(request.getRoleCode()) &&
            roleRepository.existsByRoleCode(request.getRoleCode())) {
            throw BusinessException.badRequest("角色编码已存在");
        }

        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());

        // 更新菜单权限
        if (request.getMenuIds() != null) {
            Set<Menu> menus = new HashSet<>(menuRepository.findAllById(request.getMenuIds()));
            role.setMenus(menus);
        }

        role = roleRepository.save(role);
        log.info("更新角色成功: {}", role.getRoleName());
        return toResponse(role);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("角色不存在"));
        role.setIsDeleted(true);
        roleRepository.save(role);
        log.info("删除角色成功: {}", role.getRoleName());
    }

    /**
     * 获取角色的菜单ID列表
     */
    public List<Long> getRoleMenuIds(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> BusinessException.notFound("角色不存在"));
        return role.getMenus().stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应对象
     */
    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .status(role.getStatus())
                .createdAt(role.getCreatedAt())
                .build();
    }
}
