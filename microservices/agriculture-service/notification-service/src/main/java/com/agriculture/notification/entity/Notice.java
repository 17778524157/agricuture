package com.agriculture.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 通知公告实体
 * 使用@Data注解自动生成getter、setter、toString等方法
 * 使用@EqualsAndHashCode注解，并设置callSuper为false，表示不调用父类的equals和hashCode方法
 * 使用@TableName注解指定对应的数据库表名为"notice"
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;  // 序列化版本ID，用于Java序列化

    @TableId(value = "id", type = IdType.AUTO)  // 指定id为主键，并设置为自增类型
    private Integer id;  // 通知ID

    @TableField("name")  // 数据库中的实际字段名为name
    private String name;  // 通知标题

    @TableField("content")  // 数据库中的实际字段名为content
    private String content;  // 通知内容

    @TableField("time")  // 数据库中的实际字段名为time
    private String time;  // 发布时间

    @TableField("user")  // 数据库中的实际字段名为user
    private String user;  // 发布人

    @TableField("img")  // 数据库中的实际字段名为img
    private String img;  // 封面图片

    // 为了兼容之前的代码，添加这些getter方法
    public String getTitle() {
        return this.name;
    }
    
    public String getCreateTime() {
        return this.time;
    }
    
    public String getUpdateTime() {
        return this.time;
    }
    
    // 覆盖自动生成的getImg方法
    public String getImg() {
        return this.img;
    }

}
