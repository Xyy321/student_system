package com.example.student.service;

import com.example.student.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.max-size:10485760}")
    private long maxFileSize;

    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    // 允许的文档类型
    private static final List<String> ALLOWED_DOC_TYPES = Arrays.asList(
            "application/pdf", 
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            log.info("文件上传目录: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES);
        return saveFile(file, "images");
    }

    /**
     * 上传文档
     */
    public String uploadDocument(MultipartFile file) {
        validateFile(file, ALLOWED_DOC_TYPES);
        return saveFile(file, "documents");
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES);
        return saveFile(file, "avatars");
    }

    /**
     * 通用文件上传
     */
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw BusinessException.badRequest("文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw BusinessException.badRequest("文件大小不能超过10MB");
        }
        return saveFile(file, "files");
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir, relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, List<String> allowedTypes) {
        if (file.isEmpty()) {
            throw BusinessException.badRequest("文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw BusinessException.badRequest("文件大小不能超过10MB");
        }
        String contentType = file.getContentType();
        if (!allowedTypes.contains(contentType)) {
            throw BusinessException.badRequest("不支持的文件类型: " + contentType);
        }
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, String subDir) {
        try {
            // 生成日期目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path targetDir = Paths.get(uploadDir, subDir, dateDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 生成新文件名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path targetPath = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问URL
            String fileUrl = "/uploads/" + subDir + "/" + dateDir + "/" + newFilename;
            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw BusinessException.of("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
