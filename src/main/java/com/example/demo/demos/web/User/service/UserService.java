/*
 * @Author: 
 * @Date: 2025-02-24 16:21:47
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-02-25 13:53:15
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\service\UserService.java
 */
package com.example.demo.demos.web.User.service;
import com.example.demo.demos.web.User.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    
    User findById(Long id);
    
    User save(User user);
    
    void deleteById(Long id);
    
    List<User> findByNameOrProfession(String name,String profession);

    List<User> findAllWithPagination(int pageNo, int pageSize, String sex,String profession,String name);
    
    long getTotalCount(String sex,String profession,String name);
}