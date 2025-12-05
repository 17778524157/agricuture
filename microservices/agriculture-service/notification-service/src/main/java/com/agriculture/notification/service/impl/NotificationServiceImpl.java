package com.agriculture.notification.service.impl;

import com.agriculture.notification.entity.Notice;
import com.agriculture.notification.mapper.NoticeMapper;
import com.agriculture.notification.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NoticeMapper noticeMapper;

/**
 * 获取所有公共公告的方法
 * 该方法从数据库中查询类型为"public"且状态为"active"的公告，并按时间降序排列
 *
 * @return 返回公告列表，类型为List<Notice>
 */
    @Override
    public List<Notice> getPublicNotices() {
    // 创建查询条件构造器
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
    // 设置查询条件：公告类型为"public"
        queryWrapper.eq("type", "public");
    // 设置查询条件：公告状态为"active"
        queryWrapper.eq("status", "active");
    // 设置排序条件：按时间降序排列
        queryWrapper.orderByDesc("time");
    // 执行查询并返回结果列表
        return noticeMapper.selectList(queryWrapper);
    }

    @Override
    public List<Notice> getNoticeList(Integer userId) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            // 根据我们的数据库表结构，没有user_id字段，只有user字段
            // 但由于前端可能仍然使用userId参数，我们需要处理这个情况
            queryWrapper.eq("user", userId.toString());
        }
        queryWrapper.orderByDesc("time");
        return noticeMapper.selectList(queryWrapper);
    }

    @Override
    public Notice getNoticeById(Integer id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public boolean createNotice(Notice notice) {
        try {
            // 添加详细日志
            log.debug("开始创建通知，通知信息: {}", notice);
            
            // 检查传入的通知对象是否为空
            if (notice == null) {
                log.error("创建通知失败：通知对象为空");
                return false;
            }
            
            // 检查必要字段
            if (notice.getName() == null || notice.getName().isEmpty()) {
                log.error("创建通知失败：标题不能为空");
            }
            if (notice.getContent() == null || notice.getContent().isEmpty()) {
                log.error("创建通知失败：内容不能为空");
            }
            
            // 使用time字段而不是createTime
            String currentTime = java.time.LocalDateTime.now().toString();
            notice.setTime(currentTime);
            log.debug("设置通知时间: {}", currentTime);
            
            // 执行插入操作
            int result = noticeMapper.insert(notice);
            log.debug("插入通知结果: {}, 插入后的通知ID: {}", result > 0, notice.getId());
            
            return result > 0;
        } catch (Exception e) {
            log.error("创建通知时发生异常: {}", e.getMessage(), e);
            throw e; // 重新抛出异常，以便上层能够捕获
        }
    }

    @Override
    public boolean updateNotice(Notice notice) {
        // 使用time字段而不是updateTime
        notice.setTime(java.time.LocalDateTime.now().toString());
        return noticeMapper.updateById(notice) > 0;
    }

    @Override
    public boolean deleteNotice(Integer id) {
        return noticeMapper.deleteById(id) > 0;
    }
    
    @Override
    public Page<Notice> getNoticePage(Integer pageNum, Integer pageSize, String title) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            // 使用name字段而不是title
            queryWrapper.like("name", title);
        }
        queryWrapper.orderByDesc("time");
        return noticeMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public boolean deleteBatchNotices(List<Integer> ids) {
        return noticeMapper.deleteBatchIds(ids) > 0;
    }
}
