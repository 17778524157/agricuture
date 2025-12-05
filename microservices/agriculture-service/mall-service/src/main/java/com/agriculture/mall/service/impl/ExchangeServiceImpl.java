package com.agriculture.mall.service.impl;

import com.agriculture.mall.entity.Exchange;
import com.agriculture.mall.mapper.ExchangeMapper;
import com.agriculture.mall.service.ExchangeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 兑换记录服务实现类
 */
@Service
public class ExchangeServiceImpl extends ServiceImpl<ExchangeMapper, Exchange> implements ExchangeService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    /**
     * 获取用户兑换记录分页数据
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @param userId 用户ID，可根据用户ID筛选兑换记录
     * @return 返回分页后的兑换记录数据
     */
    public Page<Exchange> getExchangePage(Integer pageNum, Integer pageSize, Long userId) {
        // 创建分页对象，设置当前页码和每页显示数量
        Page<Exchange> page = new Page<>(pageNum, pageSize);
        // 创建查询条件构造器
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
        
        // 如果用户ID不为空，则添加查询条件：按用户ID筛选
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        
        // 设置排序条件：按兑换时间降序排列
        queryWrapper.orderByDesc("exchange_time");
        
        // 执行分页查询并返回结果
        return this.page(page, queryWrapper);
    }

    @Override
    /**
     * 根据用户ID获取该用户的所有兑换记录
     * @param userId 用户ID，用于查询指定用户的兑换记录
     * @return 返回按兑换时间降序排列的兑换记录列表
     */
    public List<Exchange> getUserExchanges(Long userId) {
        // 创建查询条件构造器
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
        // 设置查询条件：用户ID等于传入的userId
        queryWrapper.eq("user_id", userId);
        // 设置排序条件：按兑换时间降序排列
        queryWrapper.orderByDesc("exchange_time");
        // 执行查询并返回结果列表
        return this.list(queryWrapper);
    }

/**
 * 获取所有交易所信息并按照交易时间降序排列
 *
 * @return 返回交易所列表，按交易时间从新到旧排序
 */
    @Override
    public List<Exchange> getAllExchanges() {
    // 创建查询条件构造器
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
    // 设置按交易时间降序排列
        queryWrapper.orderByDesc("exchange_time");
    // 执行查询并返回结果列表
        return this.list(queryWrapper);
    }

    @Override
    /**
     * 创建一个新的兑换记录
     * @param userId 用户ID
     * @param userName 用户名
     * @param productId 产品ID
     * @param productName 产品名称
     * @param productImage 产品图片
     * @param points 所需积分
     * @return Exchange 创建的兑换记录对象
     */
    public Exchange createExchange(Long userId, String userName, Long productId, String productName,
                                  String productImage, Integer points) {
        Exchange exchange = new Exchange(); // 创建兑换对象
        exchange.setUserId(userId); // 设置用户ID
        exchange.setUserName(userName); // 设置用户名
        exchange.setProductId(productId); // 设置产品ID
        exchange.setProductName(productName); // 设置产品名称
        exchange.setProductImage(productImage); // 设置产品图片
        exchange.setPoints(points); // 设置所需积分
        exchange.setExchangeTime(LocalDateTime.now().format(FORMATTER)); // 设置兑换时间
        exchange.setStatus("pending"); // 设置状态为"待处理"
        exchange.setCreateTime(LocalDateTime.now().format(FORMATTER)); // 设置创建时间
        exchange.setUpdateTime(LocalDateTime.now().format(FORMATTER)); // 设置更新时间
        
        this.save(exchange); // 保存兑换记录
        return exchange; // 返回创建的兑换对象
    }

    @Override
    public boolean updateExchangeStatus(Long exchangeId, String status) {
        Exchange exchange = this.getById(exchangeId);
        if (exchange != null) {
            exchange.setStatus(status);
            exchange.setUpdateTime(LocalDateTime.now().format(FORMATTER));
            return this.updateById(exchange);
        }
        return false;
    }

    @Override
    public boolean deleteExchange(Long exchangeId) {
        return this.removeById(exchangeId);
    }

    @Override
    public Long getTotalExchanges() {
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
        return this.count(queryWrapper);
    }

    @Override
    public Long getUserExchangeCount(Long userId) {
        QueryWrapper<Exchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.count(queryWrapper);
    }
}
