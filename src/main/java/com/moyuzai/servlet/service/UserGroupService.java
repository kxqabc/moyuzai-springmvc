package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.exception.AddUserToGroupErrorException;
import proto.MessageProtoBuf;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface UserGroupService {

    UsersResponse getAll(int offset, int resultCount);

    int getGroupAmountByUserId(long userId);

    int getAmountInGroupById(long groupId);

    ServiceData getUsersOfGroup(long groupId);

    ServiceData signoutFromGroup(long userId, long groupId);



    ServiceData queryAllUserIdOfGroup(long groupId);

    ServiceData addUserToGroup(long userId,long groupId)throws AddUserToGroupErrorException;

    void addUsersToGroup(Set<Long> userIdSet,long groupId);

    void deleteUsersOfGroup(Set<Long> userIdSet,long groupId);

    UsersResponse deleteUsersOfGroup(long id);

    int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage,
                          long userId, long groupId);

    List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId);

    //工具方法
    boolean isJoined(long groupId,long userId);

    boolean isJoined(long groupId,Set<Long> userSet);
//    proto.MessageProtoBuf.ProtoMessage getOfflineText(long userId,long groupId);

}
