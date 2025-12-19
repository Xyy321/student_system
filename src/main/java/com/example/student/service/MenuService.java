package com.example.student.service;

import com.example.student.dto.request.MenuRequest;
import com.example.student.dto.response.MenuResponse;
import com.example.student.entity.Menu;
import com.example.student.entity.User;
import com.example.student.exception.BusinessException;
import com.example.student.repository.MenuRepository;
import com.example.student.repository.UserRepository;
import com.example.student.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    /**
     * 获取当前用户的菜单树
     */
    public List<MenuResponse> getCurrentUserMenus() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        List<Menu> menus;
        if (user.getRole() != null && "ADMIN".equals(user.getRole().getRoleCode())) {
            // 管理员拥有所有菜单
            menus = menuRepository.findAllVisibleMenus();
        } else if (user.getRole() != null) {
            // 普通用户根据角色获取菜单
            menus = menuRepository.findMenusByRoleId(user.getRole().getId());
        } else {
            menus = new ArrayList<>();
        }
        
        return buildMenuTree(menus, 0L);
    }

    /**
     * 获取所有菜单（树形结构）
     */
    public List<MenuResponse> findAllTree() {
        List<Menu> menus = menuRepository.findByStatusOrderBySortOrderAsc(1);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 获取所有菜单（平铺）
     */
    public List<MenuResponse> findAll() {
        return menuRepository.findByStatusOrderBySortOrderAsc(1).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询菜单
     */
    public MenuResponse findById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("菜单不存在"));
        return toResponse(menu);
    }

    /**
     * 创建菜单
     */
    @Transactional
    public MenuResponse create(MenuRequest request) {
        Menu menu = new Menu();
        copyProperties(request, menu);
        menu = menuRepository.save(menu);
        log.info("创建菜单成功: {}", menu.getMenuName());
        return toResponse(menu);
    }

    /**
     * 更新菜单
     */
    @Transactional
    public MenuResponse update(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("菜单不存在"));
        copyProperties(request, menu);
        menu = menuRepository.save(menu);
        log.info("更新菜单成功: {}", menu.getMenuName());
        return toResponse(menu);
    }

    /**
     * 删除菜单
     */
    @Transactional
    public void delete(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("菜单不存在"));
        
        // 检查是否有子菜单
        List<Menu> children = menuRepository.findByParentIdOrderBySortOrderAsc(id);
        if (!children.isEmpty()) {
            throw BusinessException.badRequest("请先删除子菜单");
        }
        
        menuRepository.delete(menu);
        log.info("删除菜单成功: {}", menu.getMenuName());
    }

    /**
     * 构建菜单树
     */
    private List<MenuResponse> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .map(m -> {
                    MenuResponse response = toResponse(m);
                    response.setChildren(buildMenuTree(menus, m.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 复制属性
     */
    private void copyProperties(MenuRequest request, Menu menu) {
        menu.setMenuName(request.getMenuName());
        menu.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setIcon(request.getIcon());
        menu.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        menu.setMenuType(request.getMenuType() != null ? request.getMenuType() : 2);
        menu.setPermission(request.getPermission());
        menu.setVisible(request.getVisible() != null ? request.getVisible() : true);
        menu.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    /**
     * 转换为响应对象
     */
    private MenuResponse toResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .menuName(menu.getMenuName())
                .parentId(menu.getParentId())
                .path(menu.getPath())
                .component(menu.getComponent())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .menuType(menu.getMenuType())
                .permission(menu.getPermission())
                .visible(menu.getVisible())
                .status(menu.getStatus())
                .createdAt(menu.getCreatedAt())
                .children(new ArrayList<>())
                .build();
    }
}
