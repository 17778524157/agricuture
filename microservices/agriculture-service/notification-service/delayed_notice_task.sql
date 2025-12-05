-- 延迟发布公告任务表
CREATE TABLE IF NOT EXISTS `delayed_notice_task` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `notice_id` INT(11) DEFAULT NULL COMMENT '公告ID（关联notice表）',
  `title` VARCHAR(255) NOT NULL COMMENT '公告标题',
  `content` TEXT COMMENT '公告内容',
  `user` VARCHAR(100) DEFAULT NULL COMMENT '发布人',
  `img` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `scheduled_publish_time` BIGINT(20) NOT NULL COMMENT '计划发布时间（时间戳，毫秒）',
  `delay_milliseconds` BIGINT(20) NOT NULL COMMENT '延迟时间（毫秒）',
  `status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '任务状态：0-待发布，1-已发布，2-已取消，3-发布失败',
  `create_time` BIGINT(20) NOT NULL COMMENT '创建时间（时间戳，毫秒）',
  `publish_time` BIGINT(20) DEFAULT NULL COMMENT '实际发布时间（时间戳，毫秒）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '任务描述信息',
  PRIMARY KEY (`id`),
  KEY `idx_notice_id` (`notice_id`),
  KEY `idx_status` (`status`),
  KEY `idx_scheduled_publish_time` (`scheduled_publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='延迟发布公告任务表';

