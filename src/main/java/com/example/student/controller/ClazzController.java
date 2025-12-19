package com.example.student.controller;

import com.example.student.dto.request.ClazzRequest;
import com.example.student.dto.response.ClazzResponse;
import com.example.student.service.ClazzService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClazzController {

    private final ClazzService clazzService;

    /**
     * 分页查询班级
     */
    @GetMapping
    public ResultVO<PageVO<ClazzResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String department) {
        return ResultVO.success(clazzService.findPage(page, size, keyword, grade, department));
    }

    /**
     * 获取所有班级
     */
    @GetMapping("/all")
    public ResultVO<List<ClazzResponse>> findAll() {
        return ResultVO.success(clazzService.findAll());
    }

    /**
     * 根据ID查询班级
     */
    @GetMapping("/{id}")
    public ResultVO<ClazzResponse> findById(@PathVariable Long id) {
        return ResultVO.success(clazzService.findById(id));
    }

    /**
     * 根据班级编号查询班级
     */
    @GetMapping("/code/{classCode}")
    public ResultVO<ClazzResponse> findByClassCode(@PathVariable String classCode) {
        return ResultVO.success(clazzService.findByClassCode(classCode));
    }

    /**
     * 根据年级获取班级列表
     */
    @GetMapping("/grade/{grade}")
    public ResultVO<List<ClazzResponse>> findByGrade(@PathVariable String grade) {
        return ResultVO.success(clazzService.findByGrade(grade));
    }

    /**
     * 创建班级
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<ClazzResponse> create(@Valid @RequestBody ClazzRequest request) {
        return ResultVO.success("创建成功", clazzService.create(request));
    }

    /**
     * 更新班级
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<ClazzResponse> update(@PathVariable Long id, @Valid @RequestBody ClazzRequest request) {
        return ResultVO.success("更新成功", clazzService.update(id, request));
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultVO<Void> batchDelete(@RequestBody List<Long> ids) {
        clazzService.batchDelete(ids);
        return ResultVO.success("批量删除成功", null);
    }
}
