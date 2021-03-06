package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.exception.AddUserToGroupErrorException;
import com.moyuzai.servlet.exception.DeleteUserException;
import proto.MessageProtoBuf;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface UserGroupService {

    UsersResponse getAll(int offset, int resultCount);

    int getGroupAmountByUserId(long userId);

    int getAmountInGroupById(long groupId);

    ServiceData getUsersOfGroup(long groupId);

    ServiceData queryAnotherGroupOfUser(long exGroupId, long userId);

    ServiceData signoutFromGroup(long userId, long groupId)throws DeleteUserException;

    ServiceData queryAllUserIdOfGroup(long groupId);

    List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId);

    /**添加*/

    ServiceData addUserToGroup(long userId,long groupId)throws AddUserToGroupErrorException;

    void addUsersToGroup(Set<Long> userIdSet,long groupId)throws AddUserToGroupErrorException;

    int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage,
                          long userId, long groupId);
    /**删除*/

    void deleteUsersOfGroup(Set<Long> userIdSet,long groupId)throws DeleteUserException;

    UsersResponse deleteUsersOfGroup(long id);


    //工具方法
    boolean isJoined(long groupId,long userId);

    boolean isJoined(long groupId,Set<Long> userSet);

}
