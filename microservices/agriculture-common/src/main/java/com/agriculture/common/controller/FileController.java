package com.agriculture.common.controller;

import com.agriculture.common.service.FileService;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * 公共文件控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理", description = "文件上传下载管理")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @GetMapping("/**")
    @Operation(summary = "下载文件", description = "根据文件UUID下载文件")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 从请求URL中提取文件路径
        String requestURI = request.getRequestURI();
        logger.info("完整请求URI: {}", requestURI);
        
        String fileUUID = requestURI.substring("/api/file/".length());
        logger.info("提取的文件ID: {}", fileUUID);
        
        // 检查文件ID是否有效
        if (fileUUID == null || fileUUID.isEmpty() || fileUUID.equals("upload")) {
            logger.error("无效的文件ID: {}", fileUUID);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            // 检查文件是否存在于MinIO
            if (!fileService.fileExists(fileUUID)) {
                logger.error("文件不存在于MinIO: {}", fileUUID);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 获取文件信息
            StatObjectResponse fileInfo = fileService.getFileInfo(fileUUID);
            logger.info("文件信息 - 大小: {}KB, 类型: {}", fileInfo.size() / 1024, fileInfo.contentType());
            
            String contentType = fileService.getContentType(fileUUID);
            boolean isImage = fileService.isImageFile(fileUUID);
            
            // 设置响应头
            if (isImage) {
                // 对于图片文件，使用inline方式显示
                response.setContentType(contentType);
                response.addHeader("Cache-Control", "max-age=3600"); // 缓存1小时
            } else {
                // 对于其他文件，使用attachment方式下载
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
                response.setContentType(contentType);
            }
            
            // 从MinIO下载文件
            try (InputStream inputStream = fileService.downloadFile(fileUUID);
                 ServletOutputStream os = response.getOutputStream()) {
                
                // 将输入流复制到输出流
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                
                logger.info("文件{}成功: {}", isImage ? "显示" : "下载", fileUUID);
            }
            
        } catch (Exception e) {
            logger.error("文件处理失败: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传图片文件到指定文件夹")
    public Object upload(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                        @RequestParam("folder") String folder) {
        try {
            logger.info("接收到文件上传请求，文件夹: {}, 文件名: {}", folder, file.getOriginalFilename());
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                return createErrorResponse("上传文件为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return createErrorResponse("只支持图片文件上传");
            }
            
            // 检查文件大小（限制为5MB）
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) {
                return createErrorResponse("文件大小不能超过5MB");
            }
            
            // 上传文件
            String objectName = fileService.uploadFile(file, folder);
            String fileUrl = fileService.getFileUrl(objectName);
            
            logger.info("文件上传成功: {}", fileUrl);
            
            return createSuccessResponse("文件上传成功", fileUrl);
            
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            return createErrorResponse("文件上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{fileUUID}")
    @Operation(summary = "删除文件", description = "根据文件UUID删除文件")
    public Object deleteFile(@PathVariable String fileUUID) {
        try {
            fileService.deleteFile(fileUUID);
            logger.info("文件删除成功: {}", fileUUID);
            return createSuccessResponse("文件删除成功", null);
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage(), e);
            return createErrorResponse("文件删除失败: " + e.getMessage());
        }
    }

    private Object createSuccessResponse(String message, Object data) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("code", "200");
        result.put("success", true);
        result.put("message", message);
        if (data != null) {
            result.put("data", data);
        }
        return result;
    }

    private Object createErrorResponse(String message) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("code", "500");
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
