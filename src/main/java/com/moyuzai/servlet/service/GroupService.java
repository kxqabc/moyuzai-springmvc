package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.enums.MyEnum;

import java.util.Set;

public interface GroupService {

    UsersResponse getAllGroups(int offset, int limit);

    GroupResponse getGroupById(long groupId);

    Group queryGroupByGroupNameManId(String groupName, long managerId);

    UsersResponse changeGroupPic(long groupId,long managerId,int picId);

    UsersResponse changeGroupName(long groupId,long managerId,String groupName);

    UsersResponse updateGroupDate(long groupId,long managerId,Integer picId,
                                  String groupName,String addUsers,String minusUsers);

    UsersResponse createGroup(String groupName, long managerId);

    UsersResponse createGroupWithInit(Set<Long> userIdSet,long managerId,String groupName,int picId);

    /**工具方法*/
    boolean checkGroupIsExist(String groupName, long managerId);

    boolean checkGroupIsExist(long groupId);

    boolean isManagerOfThisGroup(long groupId,long managerId);

    UsersResponse deleteGroup(long managerId,long groupId);

    UsersResponse deleteGroup(long groupId);

}
