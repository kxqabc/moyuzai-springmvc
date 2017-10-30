package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;

import java.util.Set;

public interface MessagePushProxy {

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,long groupId);

    void notifyUserGroupIsDisMissed(Set<Long> userIdSet,long groupId);

    void notifyUsersGroupMessageChange(Set<Long> userSet,Group group,
                                       String addUsers);

    void notifySomeJoined(long groupId,long userId);

    void notifyUsersIsKickout(Set<Long> userIdSet,long groupId);
}
