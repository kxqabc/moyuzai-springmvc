package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import proto.MessageProtoBuf;

import java.util.List;
import java.util.Set;

public interface UserGroupService {

    UsersResponse getAll(int offset, int resultCount);

    int getAmountInGroupById(long groupId);

    UsersResponse joinGroup(long userId, long groupId);

    UsersResponse signoutFromGroup(long userId, long groupId);

    boolean isJoined(long groupId,long userId);

    List<Long> queryAllUserIdOfGroup(long groupId);

    UsersResponse addUserPicToGroup(Set<Long> userIdSet, long managerId, String groupName, int picId);

    void addUsersToGroup(Set<Long> userIdSet,long groupId);

    void deleteUsersOfGroup(Set<Long> userIdSet,long groupId);

    UsersResponse deleteUsersOfGroup(long id);

    int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage,
                          long userId, long groupId);

    List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId);

//    proto.MessageProtoBuf.ProtoMessage getOfflineText(long userId,long groupId);

}
