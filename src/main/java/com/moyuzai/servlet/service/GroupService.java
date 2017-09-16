package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

import java.util.Set;

public interface GroupService {

    GroupResponse getGroupById(long groupId);

    Group queryGroupByGroupNameManId(String groupName, long managerId);

    UsersResponse createGroup(String groupName, long managerId);

    UsersResponse createGroupWithInit(Set<Long> userIdSet,long managerId,String groupName,int picId);

    /**工具方法*/
    boolean checkGroupIsExist(String groupName, long managerId);

    boolean checkGroupIsExist(long groupId);

    UsersResponse deleteGroup(long managerId,long groupId);

}
