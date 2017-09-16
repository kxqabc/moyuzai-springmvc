package com.moyuzai.servlet.dto;

import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

/**
 * Created by xiang on 2017/7/30.
 */
public class GroupResponse {
    private boolean state;
    private Group group;
    private String managerName;

    public GroupResponse() {
    }

    public GroupResponse(MyEnum myEnum) {
        this.state = myEnum.isState();
        this.managerName = myEnum.getStateInfo();
    }

    public GroupResponse(boolean state, Group group, String managerName) {
        this.state = state;
        this.group = group;
        this.managerName = managerName;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
