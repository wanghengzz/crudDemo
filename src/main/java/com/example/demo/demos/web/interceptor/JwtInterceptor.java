package com.example.demo.demos.web.interceptor;
import com.example.demo.demos.web.utils.JwtUtil;
import com.example.demo.demos.web.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private void writeErrorResponse(HttpServletResponse response, String retCode, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(retCode, message)));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        System.out.println("请求路径: " + requestURI);
        
        // 更健壮的路径检查
        if (requestURI.startsWith("/auth/") || 
            requestURI.equals("/auth") || 
            requestURI.startsWith("/public/") || 
            requestURI.equals("/public")) {
            return true;  // 允许请求继续处理，不验证token
        }
        // Token验证逻辑
        String token = request.getHeader("Authorization");
        System.out.println("Authorization头: " + token);

        if (token == null || token.trim().isEmpty()) {
            System.out.println("Token不存在");
            writeErrorResponse(response, "401", "Token不存在，请先登录");
            return false;
        }

        if (!token.startsWith("Bearer ")) {
            System.out.println("Token格式错误");
            writeErrorResponse(response, "401", "Token格式错误");
            return false;
        }

        token = token.substring(7).trim();
        System.out.println("提取的Token: " + token);

        try {
            String username = jwtUtil.extractUsername(token);
            System.out.println("提取的用户名: " + username);

            if (username == null || username.isEmpty()) {
                System.out.println("无效的Token");
                writeErrorResponse(response, "401", "Token无效");
                return false;
            }

            String storedToken = redisTemplate.opsForValue().get(username);
            System.out.println("Redis中存储的Token: " + storedToken);

            if (storedToken == null) {
                System.out.println("Redis中没有找到Token");
                writeErrorResponse(response, "401", "Token已失效，请重新登录");
                return false;
            }

            if (!token.equals(storedToken)) {
                System.out.println("Token不匹配");
                writeErrorResponse(response, "401", "Token已失效，请重新登录");
                return false;
            }

            if (jwtUtil.isTokenExpired(token)) {
                System.out.println("Token已过期");
                writeErrorResponse(response, "401", "Token已过期，请重新登录");
                return false;
            }

            return true;  // Token有效，允许请求继续处理

        } catch (Exception e) {
            System.out.println("Token验证异常: " + e.getMessage());
            e.printStackTrace();
            writeErrorResponse(response, "401", "Token无效");
            return false;
        }
    }
}