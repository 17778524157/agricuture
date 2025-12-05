package com.agriculture.farm.controller;

import com.agriculture.common.result.Result;
import com.agriculture.farm.entity.Field;
import com.agriculture.farm.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 农田控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/farm")
@Tag(name = "农田管理", description = "农田信息管理接口")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @GetMapping("/fields")
    @Operation(summary = "获取农田列表", description = "根据用户ID获取农田列表，不传userId则获取所有农田")
    public Result<List<Field>> getFields(@RequestParam(required = false) Integer userId) {
        try {
            log.info("获取农田列表请求，userId: {}", userId);
            List<Field> fields;
            if (userId != null) {
                log.info("根据userId查询农田");
                fields = fieldService.getFieldsByUserId(userId);
            } else {
                log.info("查询所有农田");
                fields = fieldService.list();
            }
            log.info("查询到农田数量: {}", fields != null ? fields.size() : 0);
            return Result.success(fields);
        } catch (Exception e) {
            log.error("获取农田列表失败: {}", e.getMessage(), e);
            return Result.fail("获取农田数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/fields/{id}")
    @Operation(summary = "根据ID获取农田信息", description = "通过农田ID获取详细信息")
    public Result<Field> getFieldById(@PathVariable Integer id) {
        try {
            log.info("根据ID获取农田信息，id: {}", id);
            Field field = fieldService.getById(id);
            if (field != null) {
                return Result.success(field);
            } else {
                return Result.fail("农田不存在");
            }
        } catch (Exception e) {
            log.error("获取农田信息失败: {}", e.getMessage(), e);
            return Result.fail("获取农田信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/fields/{id}/statistics")
    @Operation(summary = "获取农田统计信息", description = "获取指定农田的统计数据")
    public Result<Object> getFieldStatistics(@PathVariable Integer id) {
        try {
            log.info("获取农田统计信息，id: {}", id);
            Object statistics = fieldService.getFieldStatistics(id);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取农田统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取农田统计信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/fields")
    @Operation(summary = "创建农田", description = "创建新的农田记录")
    public Result<Boolean> createField(@RequestBody Field field) {
        try {
            log.info("创建农田请求: {}", field);
            
            // 设置默认值
            if (field.getLatitude() == null) {
                field.setLatitude(39.9042); // 默认北京纬度
            }
            if (field.getLongitude() == null) {
                field.setLongitude(116.4074); // 默认北京经度
            }
            if (field.getPlantDate() == null) {
                field.setPlantDate(new java.util.Date()); // 默认当前日期
            }
            if (field.getHarvestDate() == null) {
                // 默认6个月后收获
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.MONTH, 6);
                field.setHarvestDate(cal.getTime());
            }
            if (field.getStatus() == null) {
                field.setStatus("种植中");
            }
            
            boolean result = fieldService.save(field);
            if (result) {
                log.info("农田创建成功，ID: {}", field.getId());
                return Result.success(true);
            } else {
                return Result.fail("农田创建失败");
            }
        } catch (Exception e) {
            log.error("创建农田失败: {}", e.getMessage(), e);
            return Result.fail("创建农田失败：" + e.getMessage());
        }
    }

    @PutMapping("/fields")
    @Operation(summary = "更新农田", description = "更新现有农田信息")
    public Result<Boolean> updateField(@RequestBody Field field) {
        try {
            log.info("更新农田请求: {}", field);
            boolean result = fieldService.updateById(field);
            if (result) {
                log.info("农田更新成功，ID: {}", field.getId());
                return Result.success(true);
            } else {
                return Result.fail("农田更新失败");
            }
        } catch (Exception e) {
            log.error("更新农田失败: {}", e.getMessage(), e);
            return Result.fail("更新农田失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/fields/{id}")
    @Operation(summary = "删除农田", description = "根据ID删除农田")
    public Result<Boolean> deleteField(@PathVariable Integer id) {
        try {
            log.info("删除农田请求，id: {}", id);
            boolean result = fieldService.removeById(id);
            if (result) {
                log.info("农田删除成功，ID: {}", id);
                return Result.success(true);
            } else {
                return Result.fail("农田删除失败");
            }
        } catch (Exception e) {
            log.error("删除农田失败: {}", e.getMessage(), e);
            return Result.fail("删除农田失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "农田服务健康检查接口")
    public Result<String> test() {
        try {
            log.info("测试接口被调用");
            return Result.success("农田服务测试成功");
        } catch (Exception e) {
            log.error("测试接口异常: {}", e.getMessage(), e);
            return Result.fail("测试失败：" + e.getMessage());
        }
    }
}
