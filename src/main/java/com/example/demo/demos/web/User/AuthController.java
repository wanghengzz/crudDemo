/*
 * @Author: 
 * @Date: 2025-03-26 15:56:27
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-04-01 10:10:24
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
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> data = userService.register(user);
        if(data != null){
            // 检查是否是账号已存在的错误
            if(data.containsKey("error") && "account_exists".equals(data.get("error"))){
                return Result.error("400", "账号已存在！");
            }
            return Result.success(data,"注册成功,请登录账号!");
        }else{
            return Result.error("400", "注册失败！");
        }
    }
}