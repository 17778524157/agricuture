package com.agriculture.notification.service;

import com.agriculture.notification.entity.Notice;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 获取公开通知列表
     */
    List<Notice> getPublicNotices();
    
    /**
     * 获取通知列表
     */
    List<Notice> getNoticeList(Integer userId);
    
    /**
     * 根据ID获取通知信息
     */
    Notice getNoticeById(Integer id);
    
    /**
     * 创建通知
     */
    boolean createNotice(Notice notice);
    
    /**
     * 更新通知
     */
    boolean updateNotice(Notice notice);
    
    /**
     * 删除通知
     */
    boolean deleteNotice(Integer id);
    
    /**
     * 分页查询通知
     */
    Page<Notice> getNoticePage(Integer pageNum, Integer pageSize, String title);
    
    /**
     * 批量删除通知
     */
    boolean deleteBatchNotices(List<Integer> ids);
}
