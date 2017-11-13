package com.moyuzai.servlet.dto;

import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiang on 2017/7/30.
 */
public class GroupResponse {

    private boolean state;

    private Group group;

    private String time;

    public GroupResponse() {
    }

    public GroupResponse(MyEnum myEnum) {
        this.state = myEnum.isState();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = dateFormat.format(new Date());
    }

    public GroupResponse(boolean state, Group group) {
        this.state = state;
        this.group = group;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = dateFormat.format(new Date());
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "GroupResponse{" +
                "state=" + state +
                ", group=" + group +
                ", time='" + time + '\'' +
                '}';
    }
}
