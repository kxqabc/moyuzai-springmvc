package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import proto.MessageProtoBuf;

import java.util.Set;

public interface MinaService {

    boolean isOnline(long userId);

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,
                                     String groupName,
                                     UsersResponse usersResponse);

    void notifyUserGroupIsDisMissed(Set<Long> userIdSet,long groupId);

    void notifyUsersGroupMessageChange(long groupId);
}
