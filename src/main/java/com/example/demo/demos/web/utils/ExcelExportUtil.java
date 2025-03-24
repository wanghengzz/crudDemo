package com.example.demo.demos.web.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelExportUtil {
    
    public static <T> ResponseEntity<byte[]> exportToExcel(
            String sheetName,
            String title,
            String exportUser,
            String[] columns,
            List<T> data,
            ExcelDataMapper<T> dataMapper
    ) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // 创建样式
            CellStyle styleTitle = createTitleStyle(workbook);
            CellStyle styleSubTitle = createSubTitleStyle(workbook);
            CellStyle styleContent = createContentStyle(workbook);

            // 创建标题和副标题
            createTitleRows(sheet, title, exportUser, columns.length, styleTitle, styleSubTitle);

            // 创建表头
            createHeaderRow(sheet, columns, styleContent);

            // 填充数据
            fillData(sheet, data, columns.length, styleContent, dataMapper);

            // 调整列宽
            adjustColumnWidth(sheet, columns.length);

            // 导出为字节数组
            return generateResponse(workbook, title + ".xlsx");
        }
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 20);
        style.setFont(font);
        return style;
    }

    private static CellStyle createSubTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private static CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static void createTitleRows(Sheet sheet, String title, String exportUser, int columnCount, 
            CellStyle styleTitle, CellStyle styleSubTitle) {
        // 创建标题行
        sheet.createRow(0).createCell(0).setCellValue(title);
        sheet.getRow(0).getCell(0).setCellStyle(styleTitle);

        // 创建导出时间行
        sheet.createRow(1).createCell(0).setCellValue(String.format("导出时间：%s", 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        sheet.getRow(1).getCell(0).setCellStyle(styleSubTitle);

        // 创建导出人行
        sheet.createRow(2).createCell(0).setCellValue("导出人：" + exportUser);
        sheet.getRow(2).getCell(0).setCellStyle(styleSubTitle);

        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));
    }

    private static void createHeaderRow(Sheet sheet, String[] columns, CellStyle styleContent) {
        Row headerRow = sheet.createRow(3);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(styleContent);
        }
    }

    private static <T> void fillData(Sheet sheet, List<T> data, int columnCount, 
            CellStyle styleContent, ExcelDataMapper<T> dataMapper) {
        int rowNum = 4;
        for (T item : data) {
            Row row = sheet.createRow(rowNum++);
            dataMapper.mapToRow(item, row);
            // 设置单元格样式
            for (int i = 0; i < columnCount; i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(styleContent);
                }
            }
        }
    }

    private static void adjustColumnWidth(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            if (i == 4 || i == 5) {
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 3);
            } else {
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 2);
            }
        }
    }

    private static ResponseEntity<byte[]> generateResponse(Workbook workbook, String filename) 
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", 
                new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @FunctionalInterface
    public interface ExcelDataMapper<T> {
        void mapToRow(T item, Row row);
    }
} 

    // @GetMapping("/export")
    // @ResponseBody
    // public ResponseEntity<byte[]> exportUsers() throws IOException {
    //     List<User> users = userService.findAll();
    //     String[] columns = {"ID", "姓名", "性别", "年龄", "邮箱", "地址", "电话", "职业"};
        
    //     return ExcelExportUtil.exportToExcel(
    //         "用户数据",  // sheet名
    //         "用户数据",  // 标题
    //         "牛马",      // 导出人
    //         columns,    // 列名数组
    //         users,      // 数据列表
    //         (user, row) -> {  // 数据映射函数
    //             row.createCell(0).setCellValue(user.getId());
    //             row.createCell(1).setCellValue(user.getName());
    //             row.createCell(2).setCellValue(user.getSex());
    //             row.createCell(3).setCellValue(user.getAge());
    //             row.createCell(4).setCellValue(user.getEmail());
    //             row.createCell(5).setCellValue(user.getAddress());
    //             row.createCell(6).setCellValue(user.getPhone());
    //             row.createCell(7).setCellValue(user.getProfession());
    //         }
    //     );
    // }