package com.moyuzai.servlet.entity;

import java.sql.Timestamp;

public class Group {
    private long id;
    private String groupName;
    private long managerId;
    private int picId;
    private Timestamp createTime;

    //一个user可以是多个组的管理员
    private String managerName;
    //群人数
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", managerId=" + managerId +
                ", picId=" + picId +
                ", createTime=" + createTime +
                ", managerName='" + managerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
