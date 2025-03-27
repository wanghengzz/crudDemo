package com.example.demo.demos.web.User.service;
import com.example.demo.demos.web.User.User;
import com.example.demo.demos.web.model.Result;
import com.example.demo.demos.web.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Map<String, Object> login(User user) {
      String sql = "select * from user where username=? and password=?";
        try {
            Map<String, Object> userInfo = jdbcTemplate.queryForMap(sql, user.getUsername(), user.getPassword());
            String token = jwtUtil.generateToken(user.getUsername());
            if(userInfo.isEmpty()|| token == null ){
                return null;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("username", user.getUsername());
            redisTemplate.opsForValue().set(user.getUsername(), token);
            return result;
        } catch (Exception e) {
            // 查询失败或未找到用户
            return null;
        }
    }
    @Override
public Boolean logout(User user) {
    try {
        String username = jwtUtil.extractUsername(user.getToken());
        // Delete the token from Redis instead of setting it to null
        redisTemplate.delete(username);
        return true;
    } catch (Exception e) {
        return false;
    }
}
}