package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import proto.MessageProtoBuf;

import java.util.Set;

public interface MinaService {

    boolean isOnline(long userId);

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,
                                     UsersResponse usersResponse);

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,long groupId);

    void notifyUserGroupIsDisMissed(Set<Long> userIdSet,long groupId);

    void notifyUsersGroupMessageChange(Set<Long> userSet,long groupId,String groupName,long managerId,
                                       String addUsers,int picId);

    void notifySomeJoined(long groupId,long userId);

    void notifyUsersIsKickout(Set<Long> userIdSet,long groupId);

}
