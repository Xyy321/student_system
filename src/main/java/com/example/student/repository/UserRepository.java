package com.example.student.repository;

import com.example.student.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String loginName);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1 AND u.isDeleted = false")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = ?1 AND u.isDeleted = false")
    long countByUserType(Integer userType);
}
