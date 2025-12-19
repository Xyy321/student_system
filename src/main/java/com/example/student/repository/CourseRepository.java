package com.example.student.repository;

import com.example.student.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问接口
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findBySemester(String semester);

    List<Course> findByCourseType(Integer courseType);

    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %?1% OR c.courseCode LIKE %?1%")
    List<Course> searchByKeyword(String keyword);

    @Query("SELECT c FROM Course c WHERE c.status = 1 AND c.currentStudents < c.maxStudents")
    List<Course> findAvailableCourses();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.isDeleted = false")
    long countAllCourses();

    @Query("SELECT c.courseType, COUNT(c) FROM Course c WHERE c.isDeleted = false GROUP BY c.courseType")
    List<Object[]> countCoursesByType();

    @Query("SELECT c.department, COUNT(c) FROM Course c WHERE c.isDeleted = false GROUP BY c.department")
    List<Object[]> countCoursesByDepartment();
}
