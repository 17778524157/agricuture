package com.agriculture.crop.controller;

import com.agriculture.crop.entity.Crop;
import com.agriculture.crop.service.ICropService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crop")
@Api(tags = "作物管理")
public class CropController {

    @Autowired
    private ICropService cropService;

    @ApiOperation("获取所有作物信息")
    @GetMapping("/all")
    public ResponseEntity<List<Crop>> getAllCrops() {
        List<Crop> crops = cropService.getAllCrops();
        return ResponseEntity.ok(crops);
    }

    @ApiOperation("根据ID获取作物信息")
    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCropById(@PathVariable Long id) {
        Crop crop = cropService.getCropById(id);
        return ResponseEntity.ok(crop);
    }

    @ApiOperation("根据作物名称查询")
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Crop>> getCropsByName(@PathVariable String name) {
        List<Crop> crops = cropService.getCropsByName(name);
        return ResponseEntity.ok(crops);
    }

    @ApiOperation("根据作物类型查询")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Crop>> getCropsByType(@PathVariable String type) {
        List<Crop> crops = cropService.getCropsByType(type);
        return ResponseEntity.ok(crops);
    }

    @ApiOperation("添加新作物")
    @PostMapping
    public ResponseEntity<Boolean> addCrop(@RequestBody Crop crop) {
        boolean result = cropService.addCrop(crop);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("更新作物信息")
    @PutMapping
    public ResponseEntity<Boolean> updateCrop(@RequestBody Crop crop) {
        boolean result = cropService.updateCrop(crop);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("删除作物")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCrop(@PathVariable Long id) {
        boolean result = cropService.deleteCrop(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation("批量删除作物")
    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> batchDeleteCrops(@RequestBody List<Long> ids) {
        boolean result = cropService.batchDeleteCrops(ids);
        return ResponseEntity.ok(result);
    }
}