package com.example.student.repository;

import com.example.student.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单数据访问接口
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Menu> findByStatusOrderBySortOrderAsc(Integer status);

    @Query("SELECT m FROM Menu m WHERE m.status = 1 AND m.visible = true ORDER BY m.sortOrder")
    List<Menu> findAllVisibleMenus();

    @Query("SELECT DISTINCT m FROM Menu m JOIN Role r ON m MEMBER OF r.menus WHERE r.id = ?1 AND m.status = 1 ORDER BY m.sortOrder")
    List<Menu> findMenusByRoleId(Long roleId);
}
