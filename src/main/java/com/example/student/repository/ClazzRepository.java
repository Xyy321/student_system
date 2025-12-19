package com.example.student.repository;

import com.example.student.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级数据访问接口
 */
@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long>, JpaSpecificationExecutor<Clazz> {

    Optional<Clazz> findByClassCode(String classCode);

    boolean existsByClassCode(String classCode);

    List<Clazz> findByGrade(String grade);

    List<Clazz> findByDepartment(String department);

    List<Clazz> findByMajor(String major);

    @Query("SELECT c FROM Clazz c WHERE c.status = 1 AND c.isDeleted = false")
    List<Clazz> findAllActiveClasses();

    @Query("SELECT COUNT(c) FROM Clazz c WHERE c.isDeleted = false")
    long countAllClasses();

    @Query("SELECT c.department, COUNT(c) FROM Clazz c WHERE c.isDeleted = false GROUP BY c.department")
    List<Object[]> countClassesByDepartment();
}
