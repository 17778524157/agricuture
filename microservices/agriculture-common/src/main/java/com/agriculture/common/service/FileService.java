package com.agriculture.common.service;

import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 公共文件服务类
 */
@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private com.agriculture.common.config.MinioConfig minioConfig;

    /**
     * 上传文件
     * @param file 上传的文件
     * @param folder 文件夹名称（如：notice, product, avatar等）
     * @return 文件URL
     */
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        try {
            // 确保存储桶存在
            ensureBucketExists();
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = folder + "/" + UUID.randomUUID().toString() + extension;
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            logger.info("文件上传成功: {}", fileName);
            return fileName;
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new Exception("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件（指定文件名）
     * @param file 上传的文件
     * @param folder 文件夹名称
     * @param fileName 指定文件名
     * @return 文件URL
     */
    public String uploadFile(MultipartFile file, String folder, String fileName) throws Exception {
        try {
            // 确保存储桶存在
            ensureBucketExists();
            
            // 构建完整文件名
            String fullFileName = folder + "/" + fileName;
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fullFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            logger.info("文件上传成功: {}", fullFileName);
            return fullFileName;
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new Exception("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param objectName 对象名称
     * @return 文件输入流
     */
    public InputStream downloadFile(String objectName) throws Exception {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            logger.error("文件下载失败: {}", e.getMessage(), e);
            throw new Exception("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws Exception {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            logger.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            logger.error("文件删除失败: {}", e.getMessage(), e);
            throw new Exception("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     * @param objectName 对象名称
     * @return 是否存在
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取文件信息
     * @param objectName 对象名称
     * @return 文件信息
     */
    public StatObjectResponse getFileInfo(String objectName) throws Exception {
        try {
            return minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            logger.error("获取文件信息失败: {}", e.getMessage(), e);
            throw new Exception("获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists() throws Exception {
        try {
            boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build()
            );
            
            if (!found) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .build()
                );
                logger.info("创建存储桶: {}", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            logger.error("检查/创建存储桶失败: {}", e.getMessage(), e);
            throw new Exception("检查/创建存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件URL
     * @param objectName 对象名称
     * @return 文件URL
     */
    public String getFileUrl(String objectName) {
        return "/api/file/" + objectName;
    }

    /**
     * 根据文件扩展名获取Content-Type
     */
    public String getContentType(String fileExtension) {
        if (fileExtension.contains(".")) {
            fileExtension = fileExtension.substring(fileExtension.lastIndexOf(".") + 1).toLowerCase();
        }
        
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "zip":
                return "application/zip";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 判断是否为图片文件
     */
    public boolean isImageFile(String fileName) {
        if (fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            return extension.matches("(jpg|jpeg|png|gif|webp|bmp|svg)");
        }
        return false;
    }
}
