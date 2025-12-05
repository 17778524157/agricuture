package com.agriculture.notification.mapper;

import com.agriculture.notification.entity.DelayedNoticeTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 延迟发布公告任务Mapper
 */
@Mapper
public interface DelayedNoticeTaskMapper extends BaseMapper<DelayedNoticeTask> {
}

