package com.example.student.controller;

import com.example.student.dto.request.StudentRequest;
import com.example.student.dto.response.StudentResponse;
import com.example.student.service.StudentService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理控制器
 */
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 分页查询学生
     */
    @GetMapping
    public ResultVO<PageVO<StudentResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer status) {
        return ResultVO.success(studentService.findPage(page, size, keyword, classId, status));
    }

    /**
     * 获取所有学生
     */
    @GetMapping("/all")
    public ResultVO<List<StudentResponse>> findAll() {
        return ResultVO.success(studentService.findAll());
    }

    /**
     * 根据班级ID获取学生列表
     */
    @GetMapping("/class/{classId}")
    public ResultVO<List<StudentResponse>> findByClassId(@PathVariable Long classId) {
        return ResultVO.success(studentService.findByClassId(classId));
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/{id}")
    public ResultVO<StudentResponse> findById(@PathVariable Long id) {
        return ResultVO.success(studentService.findById(id));
    }

    /**
     * 根据学号查询学生
     */
    @GetMapping("/no/{studentNo}")
    public ResultVO<StudentResponse> findByStudentNo(@PathVariable String studentNo) {
        return ResultVO.success(studentService.findByStudentNo(studentNo));
    }

    /**
     * 搜索学生
     */
    @GetMapping("/search")
    public ResultVO<List<StudentResponse>> search(@RequestParam String keyword) {
        return ResultVO.success(studentService.search(keyword));
    }

    /**
     * 创建学生
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResultVO.success("创建成功", studentService.create(request));
    }

    /**
     * 更新学生
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<StudentResponse> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResultVO.success("更新成功", studentService.update(id, request));
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> batchDelete(@RequestBody List<Long> ids) {
        studentService.batchDelete(ids);
        return ResultVO.success("批量删除成功", null);
    }
}
