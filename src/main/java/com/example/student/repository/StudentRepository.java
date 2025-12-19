package com.example.student.repository;

import com.example.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生数据访问接口
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByStudentNo(String studentNo);

    boolean existsByStudentNo(String studentNo);

    Optional<Student> findByUserId(Long userId);

    List<Student> findByClazzId(Long classId);

    Page<Student> findByClazzId(Long classId, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.name LIKE %?1% OR s.studentNo LIKE %?1%")
    List<Student> searchByKeyword(String keyword);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 1 AND s.isDeleted = false")
    long countActiveStudents();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.clazz.id = ?1 AND s.isDeleted = false")
    long countByClassId(Long classId);

    @Query("SELECT s.clazz.className, COUNT(s) FROM Student s WHERE s.isDeleted = false GROUP BY s.clazz.id, s.clazz.className")
    List<Object[]> countStudentsByClass();

    @Query("SELECT s.gender, COUNT(s) FROM Student s WHERE s.isDeleted = false GROUP BY s.gender")
    List<Object[]> countStudentsByGender();
}
