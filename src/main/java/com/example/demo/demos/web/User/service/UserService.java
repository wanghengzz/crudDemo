package com.example.demo.demos.web.User.service;

import com.example.demo.demos.web.User.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(User user);
    Boolean logout(User user);
    Map<String, Object> register(User user);
}
