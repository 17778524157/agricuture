package com.agriculture.field.controller;

import com.agriculture.common.result.Result;
import com.agriculture.field.entity.Field;
import com.agriculture.field.service.IFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field")
@Api(tags = "田地管理")
public class FieldController {

    @Autowired
    private IFieldService fieldService;

    @GetMapping
    @ApiOperation("获取所有田地信息")
    public Result<List<Field>> getAllFields() {
        List<Field> fields = fieldService.getAllFields();
        return Result.success(fields);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取田地详情")
    public Result<Field> getFieldById(@PathVariable Integer id) {
        Field field = fieldService.getFieldById(id);
        if (field != null) {
            return Result.success(field);
        } else {
            return Result.fail("未找到该田地信息");
        }
    }

    @PostMapping
    @ApiOperation("添加新的田地信息")
    public Result<Boolean> addField(@RequestBody Field field) {
        boolean success = fieldService.addField(field);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("添加失败");
        }
    }

    @PutMapping
    @ApiOperation("更新田地信息")
    public Result<Boolean> updateField(@RequestBody Field field) {
        boolean success = fieldService.updateField(field);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除田地信息")
    public Result<Boolean> deleteField(@PathVariable Integer id) {
        boolean success = fieldService.deleteField(id);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("删除失败");
        }
    }

    @GetMapping("/crop/{crop}")
    @ApiOperation("根据作物类型获取田地")
    public Result<List<Field>> getFieldsByCrop(@PathVariable String crop) {
        List<Field> fields = fieldService.getFieldsByCrop(crop);
        return Result.success(fields);
    }

    @GetMapping("/status/{status}")
    @ApiOperation("根据状态获取田地")
    public Result<List<Field>> getFieldsByStatus(@PathVariable String status) {
        List<Field> fields = fieldService.getFieldsByStatus(status);
        return Result.success(fields);
    }
}