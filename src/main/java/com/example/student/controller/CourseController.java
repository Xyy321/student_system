package com.example.student.controller;

import com.example.student.dto.request.CourseRequest;
import com.example.student.dto.response.CourseResponse;
import com.example.student.service.CourseService;
import com.example.student.util.PageVO;
import com.example.student.util.ResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 分页查询课程
     */
    @GetMapping
    public ResultVO<PageVO<CourseResponse>> findPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer courseType,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String semester) {
        return ResultVO.success(courseService.findPage(page, size, keyword, courseType, teacherId, semester));
    }

    /**
     * 获取所有课程
     */
    @GetMapping("/all")
    public ResultVO<List<CourseResponse>> findAll() {
        return ResultVO.success(courseService.findAll());
    }

    /**
     * 获取可选课程
     */
    @GetMapping("/available")
    public ResultVO<List<CourseResponse>> findAvailableCourses() {
        return ResultVO.success(courseService.findAvailableCourses());
    }

    /**
     * 根据教师ID获取课程
     */
    @GetMapping("/teacher/{teacherId}")
    public ResultVO<List<CourseResponse>> findByTeacherId(@PathVariable Long teacherId) {
        return ResultVO.success(courseService.findByTeacherId(teacherId));
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/{id}")
    public ResultVO<CourseResponse> findById(@PathVariable Long id) {
        return ResultVO.success(courseService.findById(id));
    }

    /**
     * 根据课程编号查询课程
     */
    @GetMapping("/code/{courseCode}")
    public ResultVO<CourseResponse> findByCourseCode(@PathVariable String courseCode) {
        return ResultVO.success(courseService.findByCourseCode(courseCode));
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public ResultVO<List<CourseResponse>> search(@RequestParam String keyword) {
        return ResultVO.success(courseService.search(keyword));
    }

    /**
     * 创建课程
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResultVO.success("创建成功", courseService.create(request));
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<CourseResponse> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResultVO.success("更新成功", courseService.update(id, request));
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResultVO.success("删除成功", null);
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResultVO<Void> batchDelete(@RequestBody List<Long> ids) {
        courseService.batchDelete(ids);
        return ResultVO.success("批量删除成功", null);
    }
}
