/*
 * @Author: 
 * @Date: 2025-02-24 17:07:37
 * @LastEditors: Do not edit
 * @LastEditTime: 2025-02-24 17:07:43
 * @Description: 
 * @FilePath: \demo\src\main\java\com\example\demo\demos\web\model\Result.java
 */
package com.example.demo.demos.web.model;

public class Result<T> {
    private String retCode;
    private T data;
    private String retMsg;

    public static <T> Result<T> success(T data, String retMsg) {
        Result<T> result = new Result<>();
        result.setRetCode("200");
        result.setData(data);
        if(retMsg == null){
            result.setRetMsg("查询成功");
        }else{
            result.setRetMsg(retMsg);
        }
        return result;
    }

    public static <T> Result<T> error(String retCode, String retMsg) {
        Result<T> result = new Result<>();
        result.setRetCode(retCode);
        result.setRetMsg(retMsg);
        return result;
    }

    public static <T> Result<T> warningData(String retCode, String retMsg) {
        Result<T> result = new Result<>();
        result.setRetCode(retCode);
        result.setRetMsg(retMsg);
        result.setData(null);
        return result;
    }

    // getters and setters
    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
} 