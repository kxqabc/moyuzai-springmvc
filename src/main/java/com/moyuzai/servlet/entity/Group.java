package com.moyuzai.servlet.entity;

import java.sql.Timestamp;

public class Group {
    private long id;
    private String groupName;
    private long managerId;
    private int picId;
    private Timestamp createTime;

    public Group() {
    }

    public Group(String groupName,long managerId) {
        this.groupName = groupName;
        this.managerId = managerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getManagerId() {
        return managerId;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }
}
