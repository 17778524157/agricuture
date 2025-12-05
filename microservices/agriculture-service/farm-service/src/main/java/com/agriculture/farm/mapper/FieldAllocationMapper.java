package com.agriculture.farm.mapper;

import com.agriculture.farm.entity.FieldAllocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 农田分配Mapper接口
 */
@Mapper
public interface FieldAllocationMapper extends BaseMapper<FieldAllocation> {

    /**
     * 根据用户ID查询分配记录
     * @param userId 用户ID
     * @return 分配记录列表
     */
    @Select("SELECT * FROM field_allocation WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<FieldAllocation> findByUserId(@Param("userId") Integer userId);

    /**
     * 根据农田ID查询分配记录
     * @param fieldId 农田ID
     * @return 分配记录列表
     */
    @Select("SELECT * FROM field_allocation WHERE field_id = #{fieldId} ORDER BY create_time DESC")
    List<FieldAllocation> findByFieldId(@Param("fieldId") Integer fieldId);

    /**
     * 查询冲突的分配记录
     * @param fieldId 农田ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 冲突的分配记录列表
     */
    @Select("SELECT * FROM field_allocation WHERE field_id = #{fieldId} " +
            "AND status IN ('PENDING', 'CONFIRMED') " +
            "AND ((start_date <= #{endDate} AND end_date >= #{startDate}))")
    List<FieldAllocation> findConflictingAllocations(@Param("fieldId") Integer fieldId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
}


