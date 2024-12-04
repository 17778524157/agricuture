package com.example.springboot.service.impl;

import com.example.springboot.entity.Statistic;
import com.example.springboot.mapper.StatisticMapper;
import com.example.springboot.service.IStatisticService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class StatisticServiceImpl extends ServiceImpl<StatisticMapper, Statistic> implements IStatisticService {

}
