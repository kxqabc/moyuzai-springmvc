package com.moyuzai.servlet.entity;

import java.sql.Timestamp;

public class UserGroup {
    private long id;
    private long userId;
    private long groupId;
    private String offlineText = "";//默认为空字符串，不是null
    private Timestamp createTime;

    public UserGroup() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getOfflineText() {
        return offlineText;
    }

    public void setOfflineText(String offlineText) {
        this.offlineText = offlineText;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
