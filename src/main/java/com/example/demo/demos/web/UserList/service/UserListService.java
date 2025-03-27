/*
 * @Author: 
 * @Date: 2025-02-24 16:21:47
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-02-25 13:53:15
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\service\UserService.java
 */
package com.example.demo.demos.web.UserList.service;
import com.example.demo.demos.web.UserList.UserList;
import java.util.List;

public interface UserListService {
    List<UserList> findAll();
    
    UserList findById(Long id);
    
    UserList save(UserList user);
    
    void deleteById(Long id);
    
    List<UserList> findByNameOrProfession(String name, String profession);

    List<UserList> findAllWithPagination(int pageNo, int pageSize, String sex, String profession, String name);
    
    long getTotalCount(String sex,String profession,String name);
}