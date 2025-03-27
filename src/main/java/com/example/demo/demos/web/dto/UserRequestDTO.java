package com.example.demo.demos.web.dto;
import com.example.demo.demos.web.UserList.UserList;

public  class UserRequestDTO {
    private UserList data;

    public UserList getData() {
        return data;
    }

    public void setData(UserList data) {
        this.data = data;
    }
}
