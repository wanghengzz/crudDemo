/*
 * @Author: 
 * @Date: 2025-02-24 16:22:14
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-03-11 13:53:09
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\User\service\UserServiceImpl.java
 */
package com.example.demo.demos.web.User.service;
import com.example.demo.demos.web.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM userlist";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM userlist WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            // 将所有ID更新为ID+1，注意要按照ID降序更新，避免主键冲突
            String updateSql = "UPDATE userlist SET id = id + 1 ORDER BY id DESC";
            jdbcTemplate.update(updateSql);
            
            // 新增用户，设置ID为1
            String sql = "INSERT INTO userlist (id, name, age, sex, email, address, phone, profession) VALUES (1, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                user.getName(),
                user.getAge(),
                user.getSex(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone(),
                user.getProfession()
            );
        } else {
            // 更新用户
            String sql = "UPDATE userlist SET name = ?, age = ?, sex = ?, email = ?, address = ?, phone = ?,profession = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                user.getName(),
                user.getAge(),
                user.getSex(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone(),
                user.getProfession(),
                user.getId()
            );
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM userlist WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> findByNameOrProfession(String name, String profession) {
        StringBuilder sql = new StringBuilder("SELECT * FROM userlist WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // 如果两个条件都存在，使用AND连接
        if (name != null && !name.isEmpty() && profession != null && !profession.isEmpty()) {
            sql.append(" AND name LIKE ? AND profession LIKE ?");
            params.add("%" + name + "%");
            params.add("%" + profession + "%");
        }
        // 如果只有name存在
        else if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        // 如果只有profession存在
        else if (profession != null && !profession.isEmpty()) {
            sql.append(" AND profession LIKE ?");
            params.add("%" + profession + "%");
        }
        
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class), params.toArray());
    }

    @Override
    public List<User> findAllWithPagination(int pageNo, int pageSize, String sex,String profession,String name) {
        StringBuilder sql = new StringBuilder("SELECT * FROM userlist WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (sex != null && !sex.isEmpty()) {
            sql.append(" AND sex = ?");
            params.add(sex);
        }

        if(profession != null && !profession.isEmpty()){
            sql.append(" AND profession LIKE ?");
            params.add("%" + profession + "%");
        }

        if(name != null && !name.isEmpty()){
            sql.append(" AND name LIKE ?");
            params.add("%"+name+"%");
        }
        
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNo - 1) * pageSize);
        
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class), params.toArray());
    }

    @Override
    public long getTotalCount(String sex,String profession,String name) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM userlist WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (sex != null && !sex.isEmpty()) {
            sql.append(" AND sex = ?");
            params.add(sex);
        }

        if(profession != null && !profession.isEmpty()){
            sql.append(" AND profession LIKE ?");
            params.add("%" + profession + "%");
        }

        if(name != null && !name.isEmpty()){
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

}