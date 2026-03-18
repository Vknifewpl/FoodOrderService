package com.foodorder.interceptor;

import com.alibaba.fastjson.JSON;
import com.foodorder.common.Result;
import com.foodorder.mapper.UserMapper;
import com.foodorder.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Token拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!StringUtils.hasText(token)) {
            returnError(response, "未登录或Token已过期");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            returnError(response, "Token无效或已过期");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);
        if (userId == null || role == null) {
            returnError(response, "Token解析失败");
            return false;
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && role != 1) {
            returnError(response, "无管理员权限");
            return false;
        }
        if (uri.startsWith("/super") && role != 2) {
            returnError(response, "无超级管理员权限");
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", role);
        return true;
    }

    private void returnError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(JSON.toJSONString(Result.error(401, message)));
    }
}