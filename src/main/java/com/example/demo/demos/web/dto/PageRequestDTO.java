package com.example.demo.demos.web.dto;

public class PageRequestDTO {
    private Integer pageSize;
    private Integer pageNo;
    private SearchData data;

    // getter and setter
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public SearchData getData() {
        return data;
    }

    public void setData(SearchData data) {
        this.data = data;
    }
    public static class SearchData {
        private String name;
        private String sex;
        private String profession;
        public String getSex() {
            return sex;
        }
        public void setSex(String sex) {
            this.sex = sex;
        }
        public String getProfession() {
            return profession;
        }
        public void setProfession(String profession) {
            this.profession = profession;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

    }
}