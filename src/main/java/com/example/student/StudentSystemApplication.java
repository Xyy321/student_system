package com.example.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 学生信息管理系统 - 启动类
 * 
 * @author Your Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class StudentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentSystemApplication.class, args);
        System.out.println("===========================================");
        System.out.println("   学生信息管理系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080/api");
        System.out.println("===========================================");
    }
}
