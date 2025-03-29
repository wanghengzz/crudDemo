/*
 * @Author: 
 * @Date: 2025-03-26 15:56:27
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-03-27 14:56:05
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\User\AuthController.java
 */
package com.example.demo.demos.web.User;
import com.example.demo.demos.web.User.service.UserServiceImpl;
import com.example.demo.demos.web.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> data = userService.login(user);
        if(data != null){
            return Result.success(data,"登录成功!");
        }else{
            return Result.error("400", "登录失败！");
        }
    }

    @PostMapping("/logout")
    public Result<Map<String, Object>> logout(@RequestBody User user) {
        Boolean result = userService.logout(user);
        if(result){
            return Result.success(null,"登出成功!");
        }else{
            return Result.error("400", "登出失败！");
        }
    }
}