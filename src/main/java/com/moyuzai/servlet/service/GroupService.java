package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

import java.util.Set;

public interface GroupService {

    ServiceData getAllGroups(int offset, int limit);

    ServiceData getGroupById(long groupId);

    Group queryGroupByGroupNameManId(String groupName, long managerId);

    ServiceData getGroupWithManName(long groupId);

    ServiceData changeGroupPic(long groupId,long managerId,int picId,boolean checked);

    ServiceData changeGroupName(long groupId,long managerId,String groupName,boolean checked);

    ServiceData createGroup(String groupName, long managerId);

    ServiceData deleteGroup(long managerId,long groupId);

    UsersResponse deleteGroup(long groupId);
    /**工具方法*/
    boolean checkGroupIsExist(String groupName, long managerId);

    boolean checkGroupIsExist(long groupId);

    boolean isManagerOfThisGroup(long groupId,long managerId);



}
