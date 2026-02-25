package com.foodorder.service.impl;

import com.foodorder.entity.Food;
import com.foodorder.mapper.FoodMapper;
import com.foodorder.mapper.OrderMapper;
import com.foodorder.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计Service实现
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 总订单数
        int totalOrders = orderMapper.countTotalOrders();
        result.put("totalOrders", totalOrders);

        // 总销售额
        BigDecimal totalSales = orderMapper.sumTotalSales();
        result.put("totalSales", totalSales);

        // 热门菜品TOP10
        List<Food> hotTop10 = foodMapper.selectHotTop10();
        result.put("hotTop10", hotTop10);

        return result;
    }
}
