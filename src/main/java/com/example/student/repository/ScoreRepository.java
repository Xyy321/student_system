package com.example.student.repository;

import com.example.student.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 成绩数据访问接口
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

    List<Score> findByStudentId(Long studentId);

    List<Score> findByCourseId(Long courseId);

    Optional<Score> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Score> findByStudentIdAndSemester(Long studentId, String semester);

    @Query("SELECT AVG(s.totalScore) FROM Score s WHERE s.student.id = ?1 AND s.status = 2")
    BigDecimal calculateAverageScore(Long studentId);

    @Query("SELECT AVG(s.gpa) FROM Score s WHERE s.student.id = ?1 AND s.status = 2")
    BigDecimal calculateAverageGpa(Long studentId);

    @Query("SELECT AVG(s.totalScore) FROM Score s WHERE s.course.id = ?1 AND s.status = 2")
    BigDecimal calculateCourseAverageScore(Long courseId);

    @Query("SELECT s.course.courseName, AVG(s.totalScore) FROM Score s WHERE s.student.id = ?1 AND s.status = 2 GROUP BY s.course.id, s.course.courseName")
    List<Object[]> getStudentScoresByCourse(Long studentId);

    @Query("SELECT COUNT(s) FROM Score s WHERE s.course.id = ?1 AND s.totalScore >= 60 AND s.status = 2")
    long countPassedStudents(Long courseId);

    @Query("SELECT COUNT(s) FROM Score s WHERE s.course.id = ?1 AND s.totalScore < 60 AND s.status = 2")
    long countFailedStudents(Long courseId);

    @Query("SELECT " +
           "SUM(CASE WHEN s.totalScore >= 90 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.totalScore >= 80 AND s.totalScore < 90 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.totalScore >= 70 AND s.totalScore < 80 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.totalScore >= 60 AND s.totalScore < 70 THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.totalScore < 60 THEN 1 ELSE 0 END) " +
           "FROM Score s WHERE s.course.id = ?1 AND s.status = 2")
    List<Object[]> getScoreDistribution(Long courseId);
}
