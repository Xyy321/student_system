package com.example.student.controller;

import com.example.student.dto.request.TeacherRequest;
import com.example.student.dto.response.TeacherResponse;
import com.example.student.service.TeacherService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 */
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 分页查询教师
     */
    @GetMapping
    public ResultVO<PageVO<TeacherResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer status) {
        return ResultVO.success(teacherService.findPage(page, size, keyword, department, status));
    }

    /**
     * 获取所有教师
     */
    @GetMapping("/all")
    public ResultVO<List<TeacherResponse>> findAll() {
        return ResultVO.success(teacherService.findAll());
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/{id}")
    public ResultVO<TeacherResponse> findById(@PathVariable Long id) {
        return ResultVO.success(teacherService.findById(id));
    }

    /**
     * 根据工号查询教师
     */
    @GetMapping("/no/{teacherNo}")
    public ResultVO<TeacherResponse> findByTeacherNo(@PathVariable String teacherNo) {
        return ResultVO.success(teacherService.findByTeacherNo(teacherNo));
    }

    /**
     * 搜索教师
     */
    @GetMapping("/search")
    public ResultVO<List<TeacherResponse>> search(@RequestParam String keyword) {
        return ResultVO.success(teacherService.search(keyword));
    }

    /**
     * 创建教师
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResultVO.success("创建成功", teacherService.create(request));
    }

    /**
     * 更新教师
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<TeacherResponse> update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return ResultVO.success("更新成功", teacherService.update(id, request));
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 批量删除教师
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> batchDelete(@RequestBody List<Long> ids) {
        teacherService.batchDelete(ids);
        return ResultVO.success("批量删除成功", null);
    }
}
