package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.exception.DataClassErrorException;
import com.moyuzai.servlet.exception.TargetLostException;
import proto.MessageProtoBuf;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

public interface ServiceProxy {
    /**查询*/
    UsersResponse getUserById(long id) throws DataClassErrorException;

    UsersResponse getUserByMobile(String mobile) throws DataClassErrorException;

    UsersResponse userLogin(String mobile,String password) throws DataClassErrorException;

    UsersResponse getAllUsers(int offset, int limit);

    String getUsersName(Set<Long> userIdSet);

    UsersResponse sendLoginMessage(String mobile,HttpSession httpSession);

    UsersResponse sendResetMessage(String mobile,HttpSession httpSession);
    /**修改*/
    UsersResponse justifyPassword(String mobile, String newPassword);

    /**插入*/
    UsersResponse userRegister(String userName,String mobile,String password) throws DataClassErrorException;

    UsersResponse deleteUserById(long userId);      //需不需要删除关系表中的信息？

    /**代理群组操作*/
    UsersResponse getAllGroups(int offset, int limit);

    GroupResponse getGroupById(long groupId) throws DataClassErrorException;

    Group getGroupWithManName(long groupId) throws DataClassErrorException;

    GroupResponse getGroupWithMoreDetail(long groupId) throws DataClassErrorException, TargetLostException;

    UsersResponse deleteGroup(long managerId,long groupId);

    UsersResponse deleteGroup(long groupId);

    UsersResponse changeGroupPic(long groupId, long managerId, int picId,boolean cheched);

    UsersResponse changeGroupName(long groupId,long managerId,String groupName,boolean cheched);

    UsersResponse updateGroupDate(long groupId,long managerId,Integer picId,
                                  String groupName,String addUsers,String minusUsers);

    UsersResponse createGroup(String groupName, long managerId) throws DataClassErrorException;

    UsersResponse createGroupWithInit(Set<Long> userIdSet,long managerId,String groupName,int picId);

    /**代理关系表操作*/
    UsersResponse getAll(int offset, int resultCount);

    UsersResponse getUsersOfGroup(long groupId);

    UsersResponse joinGroup(long userId, long groupId);

    UsersResponse signoutFromGroup(long userId, long groupId);

    UsersResponse queryAllUserIdOfGroup(long groupId);

    UsersResponse deleteUserOfGroup(long id);

    /**代理mina*/


}
