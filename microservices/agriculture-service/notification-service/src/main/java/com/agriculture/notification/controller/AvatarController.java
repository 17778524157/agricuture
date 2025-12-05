package com.agriculture.notification.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 头像上传控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private static final Logger logger = LoggerFactory.getLogger(AvatarController.class);

    @Autowired
    private FileService fileService;

    /**
     * 头像上传接口
     * @param file 头像文件
     * @return 返回头像URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadAvatar(@RequestParam MultipartFile file) {
        try {
            logger.info("接收到头像上传请求，原始文件名: {}", file.getOriginalFilename());
            
            // 确保文件不为空
            if (file.isEmpty()) {
                logger.error("上传文件为空");
                return Result.fail("上传文件为空");
            }

            String originalFilename = file.getOriginalFilename();
            String type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            long size = file.getSize();
            
            logger.info("头像文件信息 - 原始文件名: {}, 扩展名: {}, 大小: {}KB", originalFilename, type, size / 1024);
            
            // 检查文件类型
            if (!type.matches("(jpg|jpeg|png|gif|webp|bmp)")) {
                return Result.fail("只支持图片格式的头像上传");
            }
            
            // 检查文件大小（限制为2MB）
            if (size > 2 * 1024 * 1024) {
                return Result.fail("头像文件大小不能超过2MB");
            }
            
            // 生成唯一文件名
            String fileUUID = java.util.UUID.randomUUID().toString() + "." + type;
            
            logger.info("生成的头像文件UUID: {}", fileUUID);
            
            // 使用公共FileService上传文件到avatar文件夹
            String objectName = fileService.uploadFile(file, "avatar", fileUUID);
            
            logger.info("头像文件上传到MinIO成功: {}", objectName);
            
            // 构建文件URL，通过网关访问
            String url = "/api/file/" + objectName;
            logger.info("头像上传成功，URL: {}", url);
            
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("url", url);
            return Result.success(resultMap);
        } catch (Exception e) {
            logger.error("头像上传失败: {}", e.getMessage(), e);
            return Result.fail("头像上传失败：" + e.getMessage());
        }
    }
}
