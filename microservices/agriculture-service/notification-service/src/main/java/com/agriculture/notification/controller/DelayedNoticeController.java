package com.agriculture.notification.controller;

import com.agriculture.notification.dto.DelayedNoticeMessage;
import com.agriculture.notification.service.DelayedNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时发布公告控制器
 * 提供创建定时发布任务、取消任务、查询任务详情等功能
 */
@CrossOrigin(origins = "*") // 允许跨域请求
@RestController // 标识为RESTful控制器
@RequestMapping("/api/notification/delayed") // 请求映射路径
@Api(tags = "定时发布公告") // Swagger API文档标签
public class DelayedNoticeController {

    private static final Logger logger = LoggerFactory.getLogger(DelayedNoticeController.class); // 日志记录器

    @Autowired // 自动注入服务层
    private DelayedNoticeService delayedNoticeService;

    /**
     * 创建定时发布公告任务（按延迟时间E）
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/create")
    @ApiOperation("创建定时发布公告任务（按延迟时间）")
    public Map<String, Object> createDelayedNotice(@RequestBody Map<String, Object> request) {
    // 创建结果Map用于返回操作结果
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取请求参数
            Integer noticeId = request.get("noticeId") != null ? 
                    Integer.valueOf(request.get("noticeId").toString()) : null;
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String user = (String) request.get("user");
            String img = (String) request.get("img");
            Long delayMinutes = request.get("delayMinutes") != null ? 
                    Long.valueOf(request.get("delayMinutes").toString()) : null;
            
            // 参数验证
            if (title == null || title.isEmpty()) {
                result.put("success", false);
                result.put("message", "通知标题不能为空");
                return result;
            }
            
            if (delayMinutes == null || delayMinutes <= 0) {
                result.put("success", false);
                result.put("message", "延迟时间必须大于0分钟");
                return result;
            }
            
            // 构建消息
            DelayedNoticeMessage message = new DelayedNoticeMessage();
            message.setNoticeId(noticeId);
            message.setTitle(title);
            message.setContent(content != null ? content : "");
            message.setUser(user != null ? user : "系统");
            message.setImg(img);
            
            // 计算延迟时间（毫秒）
            Long delayMilliseconds = delayMinutes * 60 * 1000;
            
            // 发送延迟消息
            boolean success = delayedNoticeService.sendDelayedNotice(message, delayMilliseconds);
            
            if (success) {
                result.put("success", true);
                result.put("message", "定时发布任务创建成功");
                result.put("delayMinutes", delayMinutes);
                logger.info("创建定时发布公告任务成功 - Title: {}, 延迟: {}分钟", title, delayMinutes);
            } else {
                result.put("success", false);
                result.put("message", "创建定时发布任务失败");
            }
            
        } catch (Exception e) {
            logger.error("创建定时发布公告任务失败", e);
            result.put("success", false);
            result.put("message", "创建定时发布任务失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 创建定时发布公告任务（按指定时间）
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping("/create-at-time")
    @ApiOperation("创建定时发布公告任务（按指定时间）")
    public Map<String, Object> createDelayedNoticeAtTime(@RequestBody Map<String, Object> request) {
    // 创建结果映射对象
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取请求参数
            Integer noticeId = request.get("noticeId") != null ? 
                    Integer.valueOf(request.get("noticeId").toString()) : null;
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            String user = (String) request.get("user");
            String img = (String) request.get("img");
            Long scheduledPublishTime = request.get("scheduledPublishTime") != null ? 
                    Long.valueOf(request.get("scheduledPublishTime").toString()) : null;
            
            // 参数验证
            if (title == null || title.isEmpty()) {
                result.put("success", false);
                result.put("message", "通知标题不能为空");
                return result;
            }
            
            if (scheduledPublishTime == null || scheduledPublishTime <= 0) {
                result.put("success", false);
                result.put("message", "计划发布时间无效");
                return result;
            }
            
            // 构建消息
            DelayedNoticeMessage message = new DelayedNoticeMessage();
            message.setNoticeId(noticeId);
            message.setTitle(title);
            message.setContent(content != null ? content : "");
            message.setUser(user != null ? user : "系统");
            message.setImg(img);
            
            // 发送延迟消息
            boolean success = delayedNoticeService.sendDelayedNoticeAtTime(message, scheduledPublishTime);
            
            if (success) {
                result.put("success", true);
                result.put("message", "定时发布任务创建成功");
                result.put("scheduledPublishTime", scheduledPublishTime);
                logger.info("创建定时发布公告任务成功 - Title: {}, 计划发布时间: {}", title, scheduledPublishTime);
            } else {
                result.put("success", false);
                result.put("message", "创建定时发布任务失败");
            }
            
        } catch (Exception e) {
            logger.error("创建定时发布公告任务失败", e);
            result.put("success", false);
            result.put("message", "创建定时发布任务失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 取消定时发布任务
     * @param noticeId 通知ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{noticeId}")
    @ApiOperation("取消定时发布任务")
    public Map<String, Object> cancelDelayedNotice(@PathVariable Integer noticeId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = delayedNoticeService.cancelDelayedNotice(noticeId);
            
            if (success) {
                result.put("success", true);
                result.put("message", "取消定时发布任务成功");
                logger.info("取消定时发布公告任务成功 - NoticeId: {}", noticeId);
            } else {
                result.put("success", false);
                result.put("message", "取消定时发布任务失败");
            }
            
        } catch (Exception e) {
            logger.error("取消定时发布公告任务失败 - NoticeId: {}", noticeId, e);
            result.put("success", false);
            result.put("message", "取消定时发布任务失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据任务ID查询延迟发布任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/detail/task/{taskId}")
    @ApiOperation("根据任务ID查询延迟发布任务详情")
    public Map<String, Object> getTaskDetailById(@PathVariable Integer taskId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            com.agriculture.notification.entity.DelayedNoticeTask task = delayedNoticeService.getTaskById(taskId);
            
            if (task != null) {
                result.put("success", true);
                result.put("data", task);
            } else {
                result.put("success", false);
                result.put("message", "任务不存在");
            }
            
        } catch (Exception e) {
            logger.error("查询延迟发布任务详情失败 - TaskId: {}", taskId, e);
            result.put("success", false);
            result.put("message", "查询任务详情失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据公告ID查询延迟发布任务详情
     * @param noticeId 公告ID
     * @return 任务详情
     */
    @GetMapping("/detail/notice/{noticeId}")
    @ApiOperation("根据公告ID查询延迟发布任务详情")
    public Map<String, Object> getTaskDetailByNoticeId(@PathVariable Integer noticeId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            com.agriculture.notification.entity.DelayedNoticeTask task = delayedNoticeService.getTaskByNoticeId(noticeId);
            
            if (task != null) {
                result.put("success", true);
                result.put("data", task);
            } else {
                result.put("success", false);
                result.put("message", "该公告没有关联的延迟发布任务");
            }
            
        } catch (Exception e) {
            logger.error("查询延迟发布任务详情失败 - NoticeId: {}", noticeId, e);
            result.put("success", false);
            result.put("message", "查询任务详情失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 分页查询延迟发布任务列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态筛选（可选）
     * @param title 标题搜索（可选）
     * @return 任务列表
     */
    @GetMapping("/list")
    @ApiOperation("分页查询延迟发布任务列表")
    public Map<String, Object> listTasks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String title) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            com.baomidou.mybatisplus.core.metadata.IPage<com.agriculture.notification.entity.DelayedNoticeTask> page = 
                delayedNoticeService.listTasks(pageNum, pageSize, status, title);
            
            result.put("success", true);
            result.put("code", 200);
            result.put("msg", "查询成功");
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("size", page.getSize());
            result.put("current", page.getCurrent());
            result.put("pages", page.getPages());
            
        } catch (Exception e) {
            logger.error("查询延迟发布任务列表失败", e);
            result.put("success", false);
            result.put("message", "查询任务列表失败: " + e.getMessage());
        }
        
        return result;
    }
}
