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
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            writeErrorResponse(response, "401", "Token不存在，请先登录");
            return false;
        }
        
        if (!token.startsWith("Bearer ")) {
            writeErrorResponse(response, "401", "Token格式错误");
            return false;
        }

        token = token.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            String storedToken = redisTemplate.opsForValue().get(username);
            
            // 检查redis 中是否存在该用户
            if (storedToken == null) {
                writeErrorResponse(response, "401", "Token已失效，请重新登录");
                return false;
            }
            
            // 检查 redis 中的 token 是否与请求中的 token 相同
            if (!token.equals(storedToken)) {
                writeErrorResponse(response, "401", "Token已失效，请重新登录");
                return false;
            }
            // 检查 token 是否过期
            if (jwtUtil.isTokenExpired(token)) {
                writeErrorResponse(response, "401", "Token已过期，请重新登录");
                return false;
            }
            return true;
        } catch (Exception e) {
            writeErrorResponse(response, "401", "Token无效");
            return false;
        }
    }
}