package com.foodorder.config;

import com.foodorder.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录注册接口
                        "/user/login",
                        "/user/register",
                        "/admin/login",
                        "/captcha",
                        // 公开接口
                        "/food/list",
                        "/food/detail",
                        "/food/category",
                        "/food/search",
                        "/food/hot-rank",
                        "/food/praise-rank",
                        "/category/list",
                        "/comment/food",
                        // 文件上传接口
                        "/file/upload",
                        // 静态资源
                        "/images/**",
                        "/static/**",
                        // Knife4j/Swagger接口文档
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/v2/**",
                        "/v3/**",
                        "/doc.html",
                        "/favicon.ico",
                        // 错误页面
                        "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
