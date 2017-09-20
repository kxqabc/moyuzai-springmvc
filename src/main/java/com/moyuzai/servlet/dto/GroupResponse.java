package com.moyuzai.servlet.dto;

import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

/**
 * Created by xiang on 2017/7/30.
 */
public class GroupResponse {
    private boolean state;
    private Group group;

    public GroupResponse() {
    }

    public GroupResponse(MyEnum myEnum) {
        this.state = myEnum.isState();
    }

    public GroupResponse(boolean state, Group group) {
        this.state = state;
        this.group = group;
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

}
