package com.moyuzai.servlet.service;

import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.format.JsonFormat;
import com.moyuzai.servlet.dao.GroupDao;
import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.AddPicIdErrorException;
import com.moyuzai.servlet.exception.AddUserToGroupErrorException;
import com.moyuzai.servlet.exception.DeleteUserException;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proto.MessageProtoBuf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserGroupDao userGroupDao;

    @Override
    public UsersResponse getAll(int offset, int limit) {
        List<UserGroup> userGroups = userGroupDao.queryAll(offset,limit);
        if (DataFormatTransformUtil.isNullOrEmpty(userGroups))
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, userGroups);
    }

    @Override
    public int getGroupAmountByUserId(long userId) {
        return userGroupDao.queryGroupAmountByUserId(userId);
    }

    @Override
    public int getAmountInGroupById(long groupId) {
        return userGroupDao.queryAmountInGroupByGroupId(groupId);
    }

    @Override
    public ServiceData getUsersOfGroup(long groupId) {
        List<User> users = userGroupDao.queryUsersBYGroupId(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(users))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,users);
    }

    @Override
    public ServiceData queryAnotherGroupOfUser(long exGroupId, long userId) {
        UserGroup userGroup = userGroupDao.queryAnotherGroupOfUser(exGroupId,userId);
        if (DataFormatTransformUtil.isNullOrEmpty(userGroup))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,userGroup);
    }

    /**
     * 退出群组
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public ServiceData signoutFromGroup(long userId, long groupId) {
        boolean isJoined = isJoined(groupId,userId);
        /**是否是该群组成员*/
        if (!isJoined)
            return new ServiceData(false,null);
        /**退出群组动作*/
        int resultCount;
        try{
            resultCount = userGroupDao.deleteUserGroup(groupId,userId);
        }catch (SQLException e1){
            throw new DeleteUserException("删除用户发生SQLException！");
        }catch (Exception e){
            logger.error(e.getMessage());
            throw e;
        }

        if (resultCount>0){
            return new ServiceData(true,null);
            //通知其他人有人退群了
        }else{
            return new ServiceData(false,-1,null);
        }
    }

    /**
     * 用户是否已经加入该群组
     * @param groupId
     * @param userId
     * @return
     */
    @Override
    public boolean isJoined(long groupId,long userId){
        UserGroup userGroup = userGroupDao.queryUserGroup(groupId,userId);
        if (DataFormatTransformUtil.isNullOrEmpty(userGroup))      //查询结果为空，说明用户还没有加入该群组
            return false;
        else
            return true;
    }

    @Override
    public boolean isJoined(long groupId, Set<Long> userSet) {
        for (long userId:userSet){
            if (!isJoined(groupId,userSet))
                return false;
        }
        return true;
    }

    /**
     * 查询某群组内的用户id
     * @param groupId
     * @return
     */
    @Override
    public ServiceData queryAllUserIdOfGroup(long groupId) {
        List<Long> userIds = userGroupDao.queryAllUserIdOfGroup(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(userIds))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,userIds);
    }


    /**
     * 插入消息
     * @param protoMessage
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage, long userId, long groupId) {
        JsonFormat jsonFormat = new JsonFormat();       //maven中这个jar包没有导入成功!
        String json = jsonFormat.printToString(protoMessage);//将protoMessage转换为String
        int effectCount = userGroupDao.addToOfflineText(json+",",userId,groupId);           //追加信息
        return effectCount;
    }

    /**
     * 获取用户id为userId的所有群组的离线信息
     * @param userId
     * @return
     */
    @Override
    public List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId) {
        //Protobuf工具
        JsonFormat jsonFormat = new JsonFormat();
        MessageProtoBuf.ProtoMessage.Builder builder = MessageProtoBuf.ProtoMessage.newBuilder();
        ExtensionRegistry registry = ExtensionRegistry.newInstance();;

        List<MessageProtoBuf.ProtoMessage> outPut = new ArrayList<>();      //将List<String>包装成List<MessageProtoBuf.ProtoMessage>输出
        List<String> jsonList = new ArrayList<>();                          //保存的JSON LIST
        List<String> results = userGroupDao.getOfflineTextMulti(userId);    //得到多个组的离线信息，列表中每个元素代表一个群组中的离线消息
        StringBuffer stringBuffer;      //拼接results数组为一个json格式。(这里最好用stringbuffer，因为直接在循环体内拼接的话，相当于每次都要new出一个新的stringbuffer，造成浪费！)
        if (DataFormatTransformUtil.isNullOrEmpty(results)){
            return null;
        }else {
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            for (String s:results){     //s1:{xx},{xx},   s2:{xx},{xx},  --> s1+s2={xx},{xx},{xx},{xx},
                stringBuffer.append(s);
            }
            stringBuffer.append("]");
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(stringBuffer.toString());
            int length = jsonArray.length();
            Object json;
            for (int i=0;i<length;i++){
                json = jsonArray.get(i);
                if (!DataFormatTransformUtil.isNullOrEmpty(json))
                    jsonList.add(json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (String js:jsonList){
            try {
                jsonFormat.merge(js,registry,builder);
            } catch (JsonFormat.ParseException e) {
                e.printStackTrace();
            }
            outPut.add(builder.build());
        }

        /**读取离线消息后将其置空*/
        userGroupDao.updateOfflineTextMulti("",userId);
        return outPut;
    }

    @Override
    public ServiceData addUserToGroup(long userId, long groupId)throws AddUserToGroupErrorException{
        //需要调用者首先检验下群组信息和用户信息是否符合要求
        int effectCount;
        try{
            effectCount = userGroupDao.saveUserGroup(groupId,userId);
        }catch (DataAccessException de){
            logger.error("将一个用户添加到群组时发生数据库异常："+de);
            throw new AddUserToGroupErrorException("向群组中添加用户时出错！");
        }
        if (effectCount>0)
            return new ServiceData(true,null);
        else
            return new ServiceData(false,null);
    }

    /**
     * 将管理者选择的用户加入该群组
     * @param userIdSet
     * @param groupId
     */
    @Transactional
    @Override
    public void addUsersToGroup(Set<Long> userIdSet,long groupId)
    throws AddUserToGroupErrorException{
        Iterator<Long> iterator = userIdSet.iterator();
        /**将管理者选择的用户加入该群组*/
        try {
            while (iterator.hasNext()) {
                userGroupDao.saveUserGroup(groupId, iterator.next());
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new AddUserToGroupErrorException("用户加入群组出现错误！");
        }
    }

    @Override
    public void deleteUsersOfGroup(Set<Long> userIdSet, long groupId)
    throws DeleteUserException{
        Iterator<Long> iterator = userIdSet.iterator();
        /**将管理者选择的用户踢出该群组*/
        while (iterator.hasNext()) {
            try {
                int deleteCount = userGroupDao.deleteUserGroup(groupId, iterator.next());
                if (deleteCount==0)
                    throw new DeleteUserException("踢出用户错误异常！");
            }catch (Exception e){
                logger.error("踢出用户错误异常！");
                logger.error(e.getMessage());
                throw new DeleteUserException("踢出用户错误异常！");
            }
        }
    }

    @Override
    public UsersResponse deleteUsersOfGroup(long id) {
        int result = userGroupDao.deleteUserGroupById(id);
        if (result>0)
            return new UsersResponse(MyEnum.DELETE_USER_SUCCESS);
        else
            return new UsersResponse(MyEnum.DELETE_USER_FAIL);
    }

}
