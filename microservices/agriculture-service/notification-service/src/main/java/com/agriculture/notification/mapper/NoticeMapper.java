package com.agriculture.notification.mapper;

import com.agriculture.notification.entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知Mapper
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
