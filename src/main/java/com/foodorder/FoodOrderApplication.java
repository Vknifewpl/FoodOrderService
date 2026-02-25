package com.foodorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 个性化菜品推荐点餐系统启动类
 */
@SpringBootApplication
@MapperScan("com.foodorder.mapper")
@EnableScheduling
public class FoodOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderApplication.class, args);
        System.out.println("============================================");
        System.out.println("    个性化菜品推荐点餐系统启动成功!");
        System.out.println("    访问地址: http://localhost:8080");
        System.out.println("============================================");
    }
}
