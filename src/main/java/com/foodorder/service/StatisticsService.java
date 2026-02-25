package com.foodorder.service;

import java.util.Map;

/**
 * 统计Service接口
 */
public interface StatisticsService {

    /**
     * 获取统计数据
     */
    Map<String, Object> getStatistics();
}
