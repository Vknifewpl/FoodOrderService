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
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取Token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!StringUtils.hasText(token)) {
            returnError(response, "未登录或Token已过期");
            return false;
        }

        // 验证Token有效性
        if (!jwtUtil.validateToken(token)) {
            returnError(response, "Token无效或已过期");
            return false;
        }

        // 从数据库验证Token
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            returnError(response, "Token解析失败");
            return false;
        }

        // 检查数据库中的Token是否匹配
        String dbToken = userMapper.getTokenByUserId(userId);
        if (dbToken == null || !dbToken.equals(token)) {
            returnError(response, "Token已失效，请重新登录");
            return false;
        }

        // 将用户信息存入请求属性
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));

        return true;
    }

    private void returnError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(JSON.toJSONString(Result.error(401, message)));
    }
}
