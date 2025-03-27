/*
 * @Author: 
 * @Date: 2025-02-24 14:19:24
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-03-20 15:11:16
 * @Description:
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\User\BasicController.java
 */
package com.example.demo.demos.web.UserList;
import com.example.demo.demos.web.dto.UserRequestDTO;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.demos.web.UserList.service.UserListService;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.demos.web.model.Result;
import com.example.demo.demos.web.dto.PageRequestDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Controller
@RequestMapping(
    value = "/users",
    produces = "application/json;charset=UTF-8"
)
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserListController {

    @Autowired
    private UserListService userService;

    // 查询所有用户
    @GetMapping("/query")
    @ResponseBody
    public Result<Map<String, Object>> getAllUsers() {
        List<UserList> users = userService.findAll();
        long total = userService.getTotalCount(null,null,null);
        if(users.isEmpty()){
            return Result.warningData("101", "用户数据表为空！");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("data", users);
        return Result.success(response,null);
    }

    // 分页查询用户列表  http://127.0.0.1:9000/users/page
    @PostMapping(
            value = "/page",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8"
    )
    @ResponseBody
    public Result<Map<String, Object>> getUsersByPage(@RequestBody PageRequestDTO requestDTO) {
        // 设置默认值
        int pageNo = requestDTO.getPageNo() != null ? requestDTO.getPageNo() : 1;
        int pageSize = requestDTO.getPageSize() != null ? requestDTO.getPageSize() : 10;
        String name = requestDTO.getData() != null ? requestDTO.getData().getName() : null;
        String sex = requestDTO.getData() != null ? requestDTO.getData().getSex() : null;
        String profession= requestDTO.getData() != null ? requestDTO.getData().getProfession() : null;

        List<UserList> users = userService.findAllWithPagination(pageNo, pageSize, sex,profession,name);
        long total = userService.getTotalCount(sex,profession,name);

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("current", pageNo);
        response.put("size", pageSize);
        response.put("data", users);

        return Result.success(response,null);
    }

    // 根据ID查询单个用户
    @GetMapping("/queryOne")
    @ResponseBody
    public Result<UserList> getUserByName(@RequestParam Long id) {
        UserList user = userService.findById(id);
        if (user == null) {
            return Result.warningData("101", "用户不存在");
        }
        return Result.success(user,null);
    }

    // 根据名称、职业搜索用户
    @PostMapping("/search")
    @ResponseBody
        public Result<List<UserList>> searchUsersByNameOrProfession(@RequestBody UserRequestDTO requestDTO) {
        String name = requestDTO.getData().getName();
        String profession = requestDTO.getData().getProfession();
        List<UserList> users = userService.findByNameOrProfession(name,profession);
        return Result.success(users,null);
    }

    // 创建新用户
    @PostMapping("/add")
    @ResponseBody
    public Result<UserList> createUser(@RequestBody UserRequestDTO requestDTO) {
        UserList user = requestDTO.getData();
        // 创建新用户时不需要设置ID，由系统自动生成
        user.setId(null);
        return Result.success(userService.save(user), "新增成功！");
    }

    // 更新用户信息
    @PostMapping("/update")
    @ResponseBody
    public Result<UserList> updateUser(@RequestBody UserRequestDTO requestDTO) {
        UserList user = requestDTO.getData();
        if (user.getId() == null) {
            return Result.error("400", "用户ID不能为空");
        }

        UserList existingUser = userService.findById(user.getId().longValue());
        if (existingUser == null) {
            return Result.error("404", "用户不存在");
        }

        return Result.success(userService.save(user),"修改成功！");
    }

    // 删除用户
    @DeleteMapping("/delete")
    @ResponseBody
    public Result<Void> deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return Result.success(null,"删除成功！");
    }

    // 导出用户数据到Excel
    @GetMapping("/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportUsers() throws IOException {
        //
        List<UserList> users = userService.findAll();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户数据");

            //创建表名
            // 一级标题
            CellStyle styleTitle = workbook.createCellStyle();
            // 二级标题
            CellStyle styleSubTitle = workbook.createCellStyle();
            // 内容
            CellStyle styleContent = workbook.createCellStyle();
            styleContent.setAlignment(HorizontalAlignment.CENTER);
            styleTitle.setAlignment(HorizontalAlignment.CENTER);
            // 右边
            styleSubTitle.setAlignment(HorizontalAlignment.RIGHT);
            //字体大小
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 20);
            styleTitle.setFont(font);
            sheet.createRow(0).createCell(0).setCellValue("用户数据");
            sheet.getRow(0).getCell(0).setCellStyle(styleTitle);
            //格式化时间为yyyy-MM-dd HH:mm:ss
            sheet.createRow(1).createCell(0).setCellValue(String.format("导出时间：%s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            sheet.getRow(1).getCell(0).setCellStyle(styleSubTitle);
            sheet.createRow(2).createCell(0).setCellValue("导出人：" + "牛马");
            sheet.getRow(2).getCell(0).setCellStyle(styleSubTitle);
            // 创建表头
            Row headerRow = sheet.createRow(3);
            String[] columns = {"ID", "姓名", "性别", "年龄", "邮箱", "地址", "电话", "职业"};

            //合并表头
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columns.length - 1));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columns.length - 1));
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(styleContent);
            }
            // 填充数据
            int rowNum = 4;
            for (UserList user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getName());
                row.createCell(2).setCellValue(user.getSex());
                row.createCell(3).setCellValue(user.getAge());
                row.createCell(4).setCellValue(user.getEmail());
                row.createCell(5).setCellValue(user.getAddress());
                row.createCell(6).setCellValue(user.getPhone());
                row.createCell(7).setCellValue(user.getProfession());
                // 设置单元格样式
                for (int i = 0; i < columns.length; i++) {
                    row.getCell(i).setCellStyle(styleContent);
                }
            }

            // 自动调整列宽
            for (int i = 0; i < columns.length; i++) {
                //sheet.autoSizeColumn(i);
                // 根据内容自适应
                if(i == 4 || i == 5){
                    sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 3);
                }else{
                    sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 2);
                }

            }

            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());

        }
    }

    // 获取所有的年龄
    @GetMapping("/getAllAge")
    @ResponseBody
    public Result<List<Integer>> getAllAge() {
        //去掉重复的
        List<Integer> ages = userService.findAll().stream().map(UserList::getAge).sorted().distinct().collect(Collectors.toList());

        return Result.success(ages,"获取成功！");
    }

    // 导入用户数据
    @PostMapping("/import")
    @ResponseBody
    public Result<String> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("400", "上传的文件为空");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            // Skip the first 4 rows (header rows)
            for (int i = 2; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                UserList user = new UserList();
                user.setName(row.getCell(1).getStringCellValue());
                user.setSex(row.getCell(2).getStringCellValue());
                user.setAge((int) row.getCell(3).getNumericCellValue());
                user.setEmail(row.getCell(4).getStringCellValue());
                user.setAddress(row.getCell(5).getStringCellValue());
                user.setPhone(row.getCell(6).getStringCellValue());
                user.setProfession(row.getCell(7).getStringCellValue());

                userService.save(user);  // Assuming save will handle both insert and update
            }

            return Result.success("200","用户数据导入成功！");
        } catch (Exception e) {
            return Result.error("500", "导入失败：" + e.getMessage());
        }
    }

}
