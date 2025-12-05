package com.agriculture.farm.mapper;

import com.agriculture.farm.entity.FarmField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 农田Mapper接口
 */
@Mapper
public interface FarmFieldMapper extends BaseMapper<FarmField> {

    /**
     * 根据状态查询农田
     * @param status 状态
     * @return 农田列表
     */
    @Select("SELECT * FROM farm_field WHERE status = #{status}")
    List<FarmField> findByStatus(@Param("status") String status);

    /**
     * 根据位置查询农田
     * @param location 位置
     * @return 农田列表
     */
    @Select("SELECT * FROM farm_field WHERE location LIKE CONCAT('%', #{location}, '%')")
    List<FarmField> findByLocation(@Param("location") String location);

    /**
     * 根据创建者查询农田
     * @param creatorId 创建者ID
     * @return 农田列表
     */
    @Select("SELECT * FROM farm_field WHERE creator_id = #{creatorId}")
    List<FarmField> findByCreator(@Param("creatorId") Integer creatorId);
}


