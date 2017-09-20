package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import proto.MessageProtoBuf;

import java.util.Set;

public interface MinaService {

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,
                                     String groupName,
                                     UsersResponse usersResponse);

    void notifyUsersGroupMessageChange(long groupId);
}
