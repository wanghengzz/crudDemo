package com.example.demo.demos.web.dto;
import com.example.demo.demos.web.User.User;

public  class UserRequestDTO {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
