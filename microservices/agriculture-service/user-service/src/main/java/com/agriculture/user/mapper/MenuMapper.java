package com.agriculture.user.mapper;

import com.agriculture.user.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 * 该接口继承MyBatis-Plus提供的BaseMapper，获得基本的CRUD操作能力
 * 通过@Mapper注解标记为MyBatis的Mapper接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询所有菜单ID
     * @return 返回所有菜单ID的列表
     * @Select注解用于指定查询的SQL语句，此处查询sys_menu表中的所有id字段
     */
    @Select("select id from sys_menu")
    List<Integer> selectAllIds();
}