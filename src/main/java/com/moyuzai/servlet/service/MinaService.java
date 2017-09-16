package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import proto.MessageProtoBuf;

import java.util.Set;

public interface MinaService {

    void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,
                                     String groupName,
                                     UsersResponse usersResponse);
}
