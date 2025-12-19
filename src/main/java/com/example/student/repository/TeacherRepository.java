package com.example.student.repository;

import com.example.student.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师数据访问接口
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

    Optional<Teacher> findByTeacherNo(String teacherNo);

    boolean existsByTeacherNo(String teacherNo);

    Optional<Teacher> findByUserId(Long userId);

    List<Teacher> findByDepartment(String department);

    @Query("SELECT t FROM Teacher t WHERE t.name LIKE %?1% OR t.teacherNo LIKE %?1%")
    List<Teacher> searchByKeyword(String keyword);

    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.status = 1 AND t.isDeleted = false")
    long countActiveTeachers();

    @Query("SELECT t.department, COUNT(t) FROM Teacher t WHERE t.isDeleted = false GROUP BY t.department")
    List<Object[]> countTeachersByDepartment();
}
