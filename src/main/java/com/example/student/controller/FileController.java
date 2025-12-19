package com.example.student.controller;

import com.example.student.service.FileService;
import com.example.student.util.ResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public ResultVO<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ResultVO.success("上传成功", result);
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public ResultVO<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadAvatar(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ResultVO.success("上传成功", result);
    }

    /**
     * 上传文档
     */
    @PostMapping("/upload/document")
    public ResultVO<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadDocument(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ResultVO.success("上传成功", result);
    }

    /**
     * 通用文件上传
     */
    @PostMapping("/upload")
    public ResultVO<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ResultVO.success("上传成功", result);
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public ResultVO<Void> deleteFile(@RequestParam String url) {
        boolean deleted = fileService.deleteFile(url);
        if (deleted) {
            return ResultVO.success("删除成功", null);
        } else {
            return ResultVO.error("文件不存在或删除失败");
        }
    }
}
