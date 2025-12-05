package com.agriculture.mall.controller;

import com.agriculture.mall.entity.MallProduct;
import com.agriculture.mall.entity.Product;
import com.agriculture.mall.service.ProductService;
import com.agriculture.mall.service.ProductCacheService;
import com.agriculture.common.service.FileService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/mall/product")
@Api(tags = "商品管理")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductCacheService productCacheService;
    
    @Autowired
    private FileService fileService;

    @GetMapping("/page")
    @ApiOperation("分页查询商品")
    public Map<String, Object> getProductPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        
        logger.info("分页查询商品 - pageNum: {}, pageSize: {}, keyword: {}, category: {}", 
                   pageNum, pageSize, keyword, category);
        
        Page<Product> page = productService.getProductPage(pageNum, pageSize, keyword, category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        
        return result;
    }

    @GetMapping("/list")
    @ApiOperation("获取所有商品")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/admin/list")
    @ApiOperation("获取所有商品（管理端）")
    public List<Product> getAllProductsForAdmin() {
        return productService.getAllProductsForAdmin();
    }

    @GetMapping("/category/{category}")
    @ApiOperation("根据分类获取商品")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/search")
    @ApiOperation("搜索商品")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/cache/list")
    @ApiOperation("获取商品列表（带缓存）")
    public Map<String, Object> getProductListWithCache(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<MallProduct> products = productCacheService.getProductList(category, page, size);
            result.put("success", true);
            result.put("data", products);
            result.put("category", category);
            result.put("page", page);
            result.put("size", size);
            result.put("total", products != null ? products.size() : 0);
        } catch (Exception e) {
            logger.error("获取商品列表失败", e);
            result.put("success", false);
            result.put("message", "获取商品列表失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/cache/search")
    @ApiOperation("搜索商品（带缓存）")
    public Map<String, Object> searchProductsWithCache(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<MallProduct> products = productCacheService.searchProducts(keyword, page, size);
            result.put("success", true);
            result.put("data", products);
            result.put("keyword", keyword);
            result.put("page", page);
            result.put("size", size);
            result.put("total", products != null ? products.size() : 0);
        } catch (Exception e) {
            logger.error("搜索商品失败", e);
            result.put("success", false);
            result.put("message", "搜索商品失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/cache/{id}")
    @ApiOperation("根据ID获取商品（带缓存）")
    public Map<String, Object> getProductByIdWithCache(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            MallProduct product = productCacheService.getProductById(id);
            if (product != null) {
                result.put("success", true);
                result.put("data", product);
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            logger.error("获取商品详情失败", e);
            result.put("success", false);
            result.put("message", "获取商品详情失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/new")
    @ApiOperation("获取新品")
    public List<Product> getNewProducts() {
        return productService.getNewProducts();
    }

    @GetMapping("/low-stock")
    @ApiOperation("获取低库存商品")
    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取商品")
    public Product getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @ApiOperation("添加商品")
    public Map<String, Object> addProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置创建时间
            product.setCreateTime(java.time.LocalDateTime.now());
            product.setUpdateTime(product.getCreateTime());
            product.setStatus("ACTIVE"); // 默认上架
            
            boolean success = productService.save(product);
            if (success) {
                result.put("success", true);
                result.put("message", "商品添加成功");
                result.put("data", product);
            } else {
                result.put("success", false);
                result.put("message", "商品添加失败");
            }
        } catch (Exception e) {
            logger.error("添加商品失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "添加商品失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping
    @ApiOperation("更新商品")
    public Map<String, Object> updateProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置更新时间
            product.setUpdateTime(java.time.LocalDateTime.now());
            
            boolean success = productService.updateById(product);
            if (success) {
                result.put("success", true);
                result.put("message", "商品更新成功");
                result.put("data", product);
            } else {
                result.put("success", false);
                result.put("message", "商品更新失败");
            }
        } catch (Exception e) {
            logger.error("更新商品失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "更新商品失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除商品")
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = productService.removeById(id);
            if (success) {
                result.put("success", true);
                result.put("message", "商品删除成功");
            } else {
                result.put("success", false);
                result.put("message", "商品删除失败");
            }
        } catch (Exception e) {
            logger.error("删除商品失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "删除商品失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/upload")
    @ApiOperation("上传商品图片")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            logger.info("接收到商品图片上传请求，文件名: {}", file.getOriginalFilename());
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "上传文件为空");
                return result;
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "只支持图片文件上传");
                return result;
            }
            
            // 检查文件大小（限制为5MB）
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "文件大小不能超过5MB");
                return result;
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "product_" + System.currentTimeMillis() + extension;
            
            // 使用公共FileService上传文件到product文件夹
            String objectName = fileService.uploadFile(file, "product", fileName);
            
            // 构建图片URL
            String imageUrl = "/api/file/" + objectName;
            
            logger.info("商品图片上传成功: {}", imageUrl);
            
            result.put("success", true);
            result.put("message", "图片上传成功");
            result.put("url", imageUrl);
            
        } catch (Exception e) {
            logger.error("商品图片上传失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "图片上传失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/stock")
    @ApiOperation("更新商品库存")
    public Map<String, Object> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = productService.updateStock(id, stock);
            if (success) {
                result.put("success", true);
                result.put("message", "库存更新成功");
            } else {
                result.put("success", false);
                result.put("message", "库存更新失败");
            }
        } catch (Exception e) {
            logger.error("更新库存失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "更新库存失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/toggle-new")
    @ApiOperation("切换新品状态")
    public Map<String, Object> toggleNewStatus(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product product = productService.getById(id);
            if (product != null) {
                product.setIsNew(!product.getIsNew());
                product.setUpdateTime(java.time.LocalDateTime.now());
                
                boolean success = productService.updateById(product);
                if (success) {
                    result.put("success", true);
                    result.put("message", "状态更新成功");
                    result.put("isNew", product.getIsNew());
                } else {
                    result.put("success", false);
                    result.put("message", "状态更新失败");
                }
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            logger.error("切换新品状态失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}/status")
    @ApiOperation("更新商品上架状态")
    public Map<String, Object> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer status = (Integer) request.get("status");
            if (status == null || (status != 0 && status != 1)) {
                result.put("success", false);
                result.put("message", "状态值无效");
                return result;
            }
            
            Product product = productService.getById(id);
            if (product != null) {
                product.setStatus(status == 1 ? "ACTIVE" : "INACTIVE");
                product.setUpdateTime(java.time.LocalDateTime.now());
                
                boolean success = productService.updateById(product);
                if (success) {
                    result.put("success", true);
                    result.put("message", status == 1 ? "商品已上架" : "商品已下架");
                    result.put("status", product.getStatus());
                } else {
                    result.put("success", false);
                    result.put("message", "状态更新失败");
                }
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            logger.error("更新商品状态失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
        }
        return result;
    }
}