package com.agriculture.pest.controller;

import com.agriculture.common.result.Result;
import com.agriculture.pest.entity.Pest;
import com.agriculture.pest.service.IPestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pest")
@Api(tags = "病虫害管理")
public class PestController {

    @Autowired
    private IPestService pestService;

    @GetMapping("/recent/{farmId}")
    @ApiOperation("根据农场ID获取最近的病虫害数据")
    public Result<List<Pest>> getRecentByFarm(@PathVariable Integer farmId, 
                                              @RequestParam(defaultValue = "30") Integer recentDays) {
        List<Pest> pests = pestService.getRecentByFarm(farmId, recentDays);
        return Result.success(pests);
    }

    @GetMapping
    @ApiOperation("获取所有病虫害数据")
    public Result<List<Pest>> getAllPests() {
        List<Pest> pests = pestService.getAllPests();
        return Result.success(pests);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取病虫害详情")
    public Result<Pest> getPestById(@PathVariable Long id) {
        Pest pest = pestService.getPestById(id);
        if (pest != null) {
            return Result.success(pest);
        } else {
            return Result.fail("未找到该病虫害记录");
        }
    }

    @PostMapping
    @ApiOperation("添加新的病虫害记录")
    public Result<Boolean> addPest(@RequestBody Pest pest) {
        boolean success = pestService.addPest(pest);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("添加失败");
        }
    }

    @PutMapping
    @ApiOperation("更新病虫害记录")
    public Result<Boolean> updatePest(@RequestBody Pest pest) {
        boolean success = pestService.updatePest(pest);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除病虫害记录")
    public Result<Boolean> deletePest(@PathVariable Long id) {
        boolean success = pestService.deletePest(id);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("删除失败");
        }
    }
}