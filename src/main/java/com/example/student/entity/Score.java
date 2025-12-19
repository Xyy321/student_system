package com.example.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 成绩实体 - 学生选课及成绩记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "edu_score", indexes = {
    @Index(name = "idx_student_course", columnList = "student_id, course_id")
})
public class Score extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // 学生

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 课程

    @Column(name = "usual_score", precision = 5, scale = 2)
    private BigDecimal usualScore; // 平时成绩

    @Column(name = "midterm_score", precision = 5, scale = 2)
    private BigDecimal midtermScore; // 期中成绩

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore; // 期末成绩

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore; // 总成绩

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa; // 绩点

    @Column(name = "semester", length = 30)
    private String semester; // 学期

    @Column(name = "status")
    private Integer status = 0; // 0: 未录入, 1: 已录入, 2: 已确认

    @Column(name = "remark", length = 500)
    private String remark; // 备注

    /**
     * 计算总成绩（平时30% + 期中20% + 期末50%）
     */
    public void calculateTotalScore() {
        if (usualScore != null && midtermScore != null && finalScore != null) {
            this.totalScore = usualScore.multiply(new BigDecimal("0.3"))
                    .add(midtermScore.multiply(new BigDecimal("0.2")))
                    .add(finalScore.multiply(new BigDecimal("0.5")));
            this.gpa = calculateGpa(this.totalScore);
        }
    }

    /**
     * 根据总成绩计算绩点
     */
    private BigDecimal calculateGpa(BigDecimal score) {
        if (score == null) return BigDecimal.ZERO;
        double s = score.doubleValue();
        if (s >= 90) return new BigDecimal("4.0");
        else if (s >= 85) return new BigDecimal("3.7");
        else if (s >= 82) return new BigDecimal("3.3");
        else if (s >= 78) return new BigDecimal("3.0");
        else if (s >= 75) return new BigDecimal("2.7");
        else if (s >= 72) return new BigDecimal("2.3");
        else if (s >= 68) return new BigDecimal("2.0");
        else if (s >= 64) return new BigDecimal("1.5");
        else if (s >= 60) return new BigDecimal("1.0");
        else return BigDecimal.ZERO;
    }
}
