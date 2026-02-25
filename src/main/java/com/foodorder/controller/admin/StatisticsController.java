package com.foodorder.controller.admin;

import com.foodorder.common.Result;
import com.foodorder.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计Controller（管理员端）
 */
@Api(tags = "数据统计")
@RestController
@RequestMapping("/admin")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计数据
     */
    @ApiOperation(value = "获取统计数据", notes = "获取系统整体统计数据，包含用户数、菜品数、订单数、营业额等")
    @GetMapping("/stat")
    public Result<Map<String, Object>> stat() {
        Map<String, Object> result = statisticsService.getStatistics();
        return Result.success(result);
    }
}
