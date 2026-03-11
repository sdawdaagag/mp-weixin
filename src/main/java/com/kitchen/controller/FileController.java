package com.kitchen.controller;

import com.kitchen.common.Result;
import com.kitchen.entity.SysFile;
import com.kitchen.exception.BusinessException;
import com.kitchen.service.SysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 将图片文件存储到数据库中
 * 支持两种上传方式：
 * 1. multipart/form-data 格式（传统方式）
 * 2. JSON格式（base64编码，适用于云开发容器调用）
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final SysFileService sysFileService;

    /**
     * 文件上传接口（JSON格式，base64编码）
     * 适用于云开发容器调用
     * 
     * @param request 上传请求，包含fileName、fileData(base64)、contentType
     * @return 包含文件ID和访问URL的Map
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadJson(@RequestBody FileUploadRequest request) {
        // 校验参数
        if (request.getFileData() == null || request.getFileData().isEmpty()) {
            throw new BusinessException("文件数据不能为空");
        }

        try {
            // 解码base64数据
            byte[] fileData = Base64.getDecoder().decode(request.getFileData());
            
            // 生成唯一文件名
            String fileName = request.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = UUID.randomUUID().toString().replace("-", "");
            }
            
            // 获取内容类型
            String contentType = request.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/jpeg";
            }
            
            // 创建文件实体
            SysFile sysFile = new SysFile();
            sysFile.setFileName(fileName);
            sysFile.setOriginalName(fileName);
            sysFile.setContentType(contentType);
            sysFile.setFileSize((long) fileData.length);
            sysFile.setFileData(fileData);
            
            // 保存到数据库
            Long fileId = sysFileService.saveFile(sysFile);
            
            log.info("文件上传成功: id={}, fileName={}, size={}", fileId, fileName, fileData.length);
            
            // 返回文件ID和访问URL
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("fileName", fileName);
            result.put("url", "/api/file/" + fileId);
            
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            log.error("Base64解码失败", e);
            throw new BusinessException("文件数据格式错误");
        }
    }

    /**
     * 文件上传接口（multipart/form-data格式）
     * 传统方式，适用于直接上传文件
     * 
     * @param file 上传的文件
     * @return 包含文件ID和访问URL的Map
     */
    @PostMapping("/upload-file")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        // 校验文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        // 提取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        try {
            // 生成唯一文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 创建文件实体
            SysFile sysFile = new SysFile();
            sysFile.setFileName(newFileName);
            sysFile.setOriginalName(originalFilename);
            sysFile.setContentType(file.getContentType());
            sysFile.setFileSize(file.getSize());
            sysFile.setFileData(file.getBytes());
            
            // 保存到数据库
            Long fileId = sysFileService.saveFile(sysFile);
            
            log.info("文件上传成功: id={}, fileName={}, size={}", fileId, newFileName, file.getSize());
            
            // 返回文件ID和访问URL
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("fileName", newFileName);
            result.put("url", "/api/file/" + fileId);
            
            return Result.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件接口
     * 根据文件ID从数据库读取文件并返回
     * 
     * @param id 文件ID
     * @return 文件二进制数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        SysFile file = sysFileService.getFileById(id);
        
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentLength(file.getFileSize());
        headers.setContentDispositionFormData("attachment", file.getOriginalName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(file.getFileData());
    }

    /**
     * 删除文件接口
     * 
     * @param id 文件ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Long id) {
        sysFileService.deleteFile(id);
        return Result.success();
    }

    /**
     * 文件上传请求DTO
     * 用于接收JSON格式的上传请求
     */
    @lombok.Data
    public static class FileUploadRequest {
        /**
         * 文件名
         */
        private String fileName;
        
        /**
         * 文件数据（base64编码）
         */
        private String fileData;
        
        /**
         * 内容类型（如 image/jpeg）
         */
        private String contentType;
    }
}
