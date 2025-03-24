/*
 * @Author: 
 * @Date: 2025-03-04 10:59:30
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-03-04 13:28:06
 * @Description:
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\enume\EnumController.java
 */
package com.example.demo.demos.web.enume;

import com.example.demo.demos.web.dto.PageRequestDTO;
import com.example.demo.demos.web.enume.service.EnumeService;
import com.example.demo.demos.web.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/enume")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnumController {
    @Autowired
    EnumeService enumeService;

    @PostMapping("/query")
    @ResponseBody
    public Result<Map<String, List<Enume>>> getEnume() {
        return Result.success(enumeService.getEnume(),"查询成功");
    }
}
