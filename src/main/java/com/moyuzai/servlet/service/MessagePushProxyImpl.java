package com.moyuzai.servlet.service;

import com.moyuzai.servlet.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class MessagePushProxyImpl implements MessagePushProxy {

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;

    @Override
    public void notifyUserIsPulledIntoGroup(Set<Long> userIdSet, long groupId) {

    }

    @Override
    public void notifyUserGroupIsDisMissed(Set<Long> userIdSet, long groupId) {

    }

    @Override
    public void notifyUsersGroupMessageChange(Set<Long> userSet, Group group, String addUsers) {

    }

    @Override
    public void notifySomeJoined(long groupId, long userId) {

    }

    @Override
    public void notifyUsersIsKickout(Set<Long> userIdSet, long groupId) {

    }
}
