package com.agriculture.notification.controller;

import com.agriculture.notification.entity.Notice;
import com.agriculture.notification.service.NotificationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知公告控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/api/notification", "/api/notice"})
@Api(tags = "通知公告")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/public")
    @ApiOperation("获取公开通知列表")
    public List<Notice> getPublicNotices() {
        return notificationService.getPublicNotices();
    }

    @GetMapping("/list")
    @ApiOperation("获取通知列表")
    public List<Notice> getNoticeList(@RequestParam(required = false) Integer userId) {
        return notificationService.getNoticeList(userId);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取通知信息")
    public Notice getNoticeById(@PathVariable Integer id) {
        return notificationService.getNoticeById(id);
    }

    @PostMapping
    @ApiOperation("创建通知")
    public boolean createNotice(@Valid @RequestBody Notice notice) {
        return notificationService.createNotice(notice);
    }

    @PutMapping
    @ApiOperation("更新通知")
    public boolean updateNotice(@Valid @RequestBody Notice notice) {
        return notificationService.updateNotice(notice);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除通知")
    public boolean deleteNotice(@PathVariable Integer id) {
        return notificationService.deleteNotice(id);
    }
    
    @GetMapping("/page")
    public Map<String, Object> getNoticePage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String name) {
        logger.debug("调用getNoticePage方法 - pageNum: {}, pageSize: {}, title: {}, name: {}", pageNum, pageSize, title, name);
        
        // 确保参数不为空
        if (pageNum == null) {
            pageNum = 1;
            logger.debug("pageNum为null，设置默认值: {}", pageNum);
        }
        if (pageSize == null) {
            pageSize = 10;
            logger.debug("pageSize为null，设置默认值: {}", pageSize);
        }
        
        // 优先使用name参数，兼容前端调用
        String queryTitle = name != null ? name : title;
        logger.debug("最终查询标题: {}", queryTitle);
        
        try {
            logger.debug("准备调用notificationService.getNoticePage");
            Page<Notice> pageResult = notificationService.getNoticePage(pageNum, pageSize, queryTitle);
            logger.debug("调用notificationService.getNoticePage成功，返回记录数: {}, 总记录数: {}", 
                        pageResult.getRecords().size(), pageResult.getTotal());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", pageResult.getRecords());
            result.put("total", pageResult.getTotal());
            logger.debug("构造返回结果成功");
            return result;
        } catch (Exception e) {
            logger.error("获取通知分页数据失败: {}", e.getMessage(), e);
            throw e; // 重新抛出异常，让上层处理
        }
    }
    
    @PostMapping("/del/batch")
    @ApiOperation("批量删除通知")
    public boolean deleteBatchNotices(@RequestBody List<Integer> ids) {
        return notificationService.deleteBatchNotices(ids);
    }
    
}
