package com.example.demo.demos.web.User.service;
import com.example.demo.demos.web.User.User;
import com.example.demo.demos.web.model.Result;
import com.example.demo.demos.web.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
            redisTemplate.delete(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public Map<String, Object> register(User user) {
        // 查询账号是否已存在
        String sql = "select * from user where username=?";
        try {
            Map<String, Object> userInfo = jdbcTemplate.queryForMap(sql, user.getUsername());
            if (!userInfo.isEmpty()) {
                // 账号已存在，返回特定错误信息
                Map<String, Object> result = new HashMap<>();
                result.put("error", "account_exists");
                return result;
            }
        } catch (Exception e) {
            // 账号不存在，可以注册
            try {
                String sqls = "insert into user (username, password) values (?, ?)";
                jdbcTemplate.update(sqls, user.getUsername(), user.getPassword());
                Map<String, Object> result = new HashMap<>();
                result.put("username", user.getUsername());
                return result;
            }catch (Exception e1){
              log.error("e: ", e1);
              return null;
            }
        }
        return null;
    }
}