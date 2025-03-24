/*
 * @Author: 
 * @Date: 2025-03-04 10:22:13
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-03-11 14:42:18
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\enume\service\EnumeServiceImpl.java
 */
package com.example.demo.demos.web.enume.service;
import com.example.demo.demos.web.enume.Enume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EnumeServiceImpl implements EnumeService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, List<Enume>> getEnume() {
        String sql = "select * from enum";
        List<Enume> enumeList = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Enume enume = new Enume();
            enume.setId(rs.getInt("id"));
            enume.setEnumKey(rs.getString("enumKey"));
            enume.setEnumValue(rs.getString("enumValue"));
            enume.setEnumDesc(rs.getString("enumDesc"));
            enume.setMark(rs.getString("mark"));
            return enume;
        });
        
        return enumeList.stream().collect(Collectors.groupingBy(Enume::getEnumKey));
    }
}
