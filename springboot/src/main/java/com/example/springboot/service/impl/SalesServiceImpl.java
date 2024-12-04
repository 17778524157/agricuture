package com.example.springboot.service.impl;

import com.example.springboot.entity.Sales;
import com.example.springboot.mapper.SalesMapper;
import com.example.springboot.service.ISalesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 */
@Service
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements ISalesService {

}
