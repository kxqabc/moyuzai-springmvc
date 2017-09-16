package com.moyuzai.servlet.entity;

import java.sql.Timestamp;

/**
 * Created by kk on 17-5-8.
 */
public class User {
    private long id;
    private String userName;
    private String mobile;
    private String password;
    private Timestamp createTime;

    public User() {
    }

    public User(String userName, String mobile, String password) {
        this.userName = userName;
        this.mobile = mobile;
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
