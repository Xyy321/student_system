package com.example.student.config;

import com.example.student.entity.*;
import com.example.student.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ClazzRepository clazzRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 只在数据库为空时初始化
        if (roleRepository.count() == 0) {
            log.info("开始初始化系统数据...");
            initRoles();
            initMenus();
            initUsers();
            initClasses();
            initTeachers();
            initStudents();
            initCourses();
            log.info("系统数据初始化完成！");
        }
    }

    private void initRoles() {
        List<Role> roles = Arrays.asList(
            Role.builder().roleName("超级管理员").roleCode("ADMIN").description("系统管理员，拥有所有权限").status(1).build(),
            Role.builder().roleName("教师").roleCode("TEACHER").description("教师角色").status(1).build(),
            Role.builder().roleName("学生").roleCode("STUDENT").description("学生角色").status(1).build()
        );
        roleRepository.saveAll(roles);
        log.info("角色数据初始化完成");
    }

    private void initMenus() {
        // 一级菜单
        Menu dashboard = Menu.builder().menuName("首页").parentId(0L).path("/dashboard").component("Dashboard").icon("House").sortOrder(1).menuType(2).status(1).build();
        Menu system = Menu.builder().menuName("系统管理").parentId(0L).path("/system").icon("Setting").sortOrder(2).menuType(1).status(1).build();
        Menu education = Menu.builder().menuName("教学管理").parentId(0L).path("/education").icon("School").sortOrder(3).menuType(1).status(1).build();
        Menu statistics = Menu.builder().menuName("数据统计").parentId(0L).path("/statistics").icon("DataAnalysis").sortOrder(4).menuType(1).status(1).build();
        Menu about = Menu.builder().menuName("关于系统").parentId(0L).path("/about").icon("InfoFilled").sortOrder(5).menuType(1).status(1).build();

        menuRepository.saveAll(Arrays.asList(dashboard, system, education, statistics, about));

        // 系统管理子菜单
        Long systemId = system.getId();
        menuRepository.saveAll(Arrays.asList(
            Menu.builder().menuName("用户管理").parentId(systemId).path("/system/user").component("system/User").icon("User").sortOrder(1).menuType(2).status(1).build(),
            Menu.builder().menuName("角色管理").parentId(systemId).path("/system/role").component("system/Role").icon("UserFilled").sortOrder(2).menuType(2).status(1).build(),
            Menu.builder().menuName("菜单管理").parentId(systemId).path("/system/menu").component("system/Menu").icon("Menu").sortOrder(3).menuType(2).status(1).build()
        ));

        // 教学管理子菜单
        Long educationId = education.getId();
        menuRepository.saveAll(Arrays.asList(
            Menu.builder().menuName("学生管理").parentId(educationId).path("/education/student").component("education/Student").icon("Avatar").sortOrder(1).menuType(2).status(1).build(),
            Menu.builder().menuName("教师管理").parentId(educationId).path("/education/teacher").component("education/Teacher").icon("User").sortOrder(2).menuType(2).status(1).build(),
            Menu.builder().menuName("班级管理").parentId(educationId).path("/education/class").component("education/Class").icon("OfficeBuilding").sortOrder(3).menuType(2).status(1).build(),
            Menu.builder().menuName("课程管理").parentId(educationId).path("/education/course").component("education/Course").icon("Reading").sortOrder(4).menuType(2).status(1).build(),
            Menu.builder().menuName("成绩管理").parentId(educationId).path("/education/score").component("education/Score").icon("Document").sortOrder(5).menuType(2).status(1).build()
        ));

        // 数据统计子菜单
        Long statisticsId = statistics.getId();
        menuRepository.saveAll(Arrays.asList(
            Menu.builder().menuName("学生统计").parentId(statisticsId).path("/statistics/student").component("statistics/Student").icon("PieChart").sortOrder(1).menuType(2).status(1).build(),
            Menu.builder().menuName("成绩分析").parentId(statisticsId).path("/statistics/score").component("statistics/Score").icon("TrendCharts").sortOrder(2).menuType(2).status(1).build()
        ));

        // 关于系统子菜单
        Long aboutId = about.getId();
        menuRepository.saveAll(Arrays.asList(
            Menu.builder().menuName("项目介绍").parentId(aboutId).path("/about/project").component("about/Project").icon("Files").sortOrder(1).menuType(2).status(1).build(),
            Menu.builder().menuName("团队介绍").parentId(aboutId).path("/about/team").component("about/Team").icon("UserFilled").sortOrder(2).menuType(2).status(1).build()
        ));

        log.info("菜单数据初始化完成");
    }

    private void initUsers() {
        Role adminRole = roleRepository.findByRoleCode("ADMIN").orElse(null);
        Role teacherRole = roleRepository.findByRoleCode("TEACHER").orElse(null);
        Role studentRole = roleRepository.findByRoleCode("STUDENT").orElse(null);

        // 管理员账号
        User admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("123456"))
            .realName("系统管理员")
            .email("admin@example.com")
            .userType(1)
            .role(adminRole)
            .status(1)
            .build();
        userRepository.save(admin);

        // 教师账号
        User teacher = User.builder()
            .username("teacher")
            .password(passwordEncoder.encode("123456"))
            .realName("张老师")
            .email("teacher@example.com")
            .userType(2)
            .role(teacherRole)
            .status(1)
            .build();
        userRepository.save(teacher);

        // 学生账号
        User student = User.builder()
            .username("student")
            .password(passwordEncoder.encode("123456"))
            .realName("李同学")
            .email("student@example.com")
            .userType(3)
            .role(studentRole)
            .status(1)
            .build();
        userRepository.save(student);

        log.info("用户数据初始化完成（admin/123456, teacher/123456, student/123456）");
    }

    private void initClasses() {
        List<Clazz> classes = Arrays.asList(
            Clazz.builder().className("软件工程2401班").classCode("SE2401").grade("2024级").major("软件工程").department("计算机学院").studentCount(0).status(1).build(),
            Clazz.builder().className("软件工程2402班").classCode("SE2402").grade("2024级").major("软件工程").department("计算机学院").studentCount(0).status(1).build(),
            Clazz.builder().className("大数据2401班").classCode("BD2401").grade("2024级").major("大数据").department("计算机学院").studentCount(0).status(1).build()
        );
        clazzRepository.saveAll(classes);
        log.info("班级数据初始化完成");
    }

    private void initTeachers() {
        User teacherUser = userRepository.findByUsername("teacher").orElse(null);
        
        List<Teacher> teachers = Arrays.asList(
            Teacher.builder()
                .teacherNo("T20240001")
                .name("张三")
                .gender(1)
                .phone("13800138001")
                .email("zhangsan@example.com")
                .department("计算机学院")
                .title("副教授")
                .education("博士")
                .entryDate(LocalDate.of(2020, 9, 1))
                .status(1)
                .user(teacherUser)
                .build(),
            Teacher.builder()
                .teacherNo("T20240002")
                .name("李四")
                .gender(2)
                .phone("13800138002")
                .email("lisi@example.com")
                .department("计算机学院")
                .title("讲师")
                .education("硕士")
                .entryDate(LocalDate.of(2022, 9, 1))
                .status(1)
                .build()
        );
        teacherRepository.saveAll(teachers);
        log.info("教师数据初始化完成");
    }

    private void initStudents() {
        User studentUser = userRepository.findByUsername("student").orElse(null);
        Clazz clazz1 = clazzRepository.findByClassCode("SE2401").orElse(null);
        Clazz clazz2 = clazzRepository.findByClassCode("SE2402").orElse(null);

        List<Student> students = Arrays.asList(
            Student.builder()
                .studentNo("2024001001")
                .name("王五")
                .gender(1)
                .birthDate(LocalDate.of(2005, 3, 15))
                .phone("13900139001")
                .email("wangwu@example.com")
                .clazz(clazz1)
                .enrollmentDate(LocalDate.of(2024, 9, 1))
                .status(1)
                .user(studentUser)
                .build(),
            Student.builder()
                .studentNo("2024001002")
                .name("赵六")
                .gender(2)
                .birthDate(LocalDate.of(2005, 7, 20))
                .phone("13900139002")
                .email("zhaoliu@example.com")
                .clazz(clazz1)
                .enrollmentDate(LocalDate.of(2024, 9, 1))
                .status(1)
                .build(),
            Student.builder()
                .studentNo("2024001003")
                .name("钱七")
                .gender(1)
                .birthDate(LocalDate.of(2005, 11, 5))
                .phone("13900139003")
                .email("qianqi@example.com")
                .clazz(clazz2)
                .enrollmentDate(LocalDate.of(2024, 9, 1))
                .status(1)
                .build()
        );
        studentRepository.saveAll(students);

        // 更新班级学生人数
        if (clazz1 != null) {
            clazz1.setStudentCount(2);
            clazzRepository.save(clazz1);
        }
        if (clazz2 != null) {
            clazz2.setStudentCount(1);
            clazzRepository.save(clazz2);
        }

        log.info("学生数据初始化完成");
    }

    private void initCourses() {
        Teacher teacher1 = teacherRepository.findByTeacherNo("T20240001").orElse(null);
        Teacher teacher2 = teacherRepository.findByTeacherNo("T20240002").orElse(null);

        List<Course> courses = Arrays.asList(
            Course.builder()
                .courseCode("CS1001")
                .courseName("Web技术")
                .credit(new BigDecimal("3.0"))
                .hours(64)
                .courseType(1)
                .teacher(teacher1)
                .department("计算机学院")
                .semester("2024-2025-1")
                .maxStudents(60)
                .currentStudents(0)
                .description("本课程讲授Web开发技术，包括HTML、CSS、JavaScript、Vue等前端技术和Spring Boot后端技术。")
                .status(1)
                .build(),
            Course.builder()
                .courseCode("CS1002")
                .courseName("数据库原理")
                .credit(new BigDecimal("3.0"))
                .hours(48)
                .courseType(1)
                .teacher(teacher2)
                .department("计算机学院")
                .semester("2024-2025-1")
                .maxStudents(60)
                .currentStudents(0)
                .description("本课程讲授数据库基本原理和SQL语言。")
                .status(1)
                .build(),
            Course.builder()
                .courseCode("CS1003")
                .courseName("Java程序设计")
                .credit(new BigDecimal("4.0"))
                .hours(64)
                .courseType(1)
                .teacher(teacher1)
                .department("计算机学院")
                .semester("2024-2025-1")
                .maxStudents(60)
                .currentStudents(0)
                .description("本课程讲授Java语言基础和面向对象编程。")
                .status(1)
                .build()
        );
        courseRepository.saveAll(courses);
        log.info("课程数据初始化完成");
    }
}
