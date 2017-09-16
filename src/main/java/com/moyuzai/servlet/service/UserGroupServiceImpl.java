package com.moyuzai.servlet.service;

import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.format.JsonFormat;
import com.moyuzai.servlet.dao.GroupDao;
import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.AddPicIdErrorException;
import com.moyuzai.servlet.exception.AddUserToGroupErrorException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proto.MessageProtoBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    /**
     * 加入群组
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public UsersResponse joinGroup(long userId, long groupId) {
        /**判断用户、群组是否存在*/
        boolean isUserExist = userService.isUserExist(userId);
        boolean isGroupExist = groupService.checkGroupIsExist(groupId);
        if (!isUserExist || !isGroupExist)
            return new UsersResponse(MyEnum.GROUP_USER_INFO_ERROR);
        /**判断用户是否已经加入群组，放置重复加入同一个群组*/
        boolean isJoined = isJoined(groupId,userId);
        if (isJoined)
            return new UsersResponse(MyEnum.IS_JOINED);
        /**执行加入动作*/
        int resultCount = userGroupDao.saveUserGroup(groupId,userId);
        if (resultCount>0)
            return new UsersResponse(MyEnum.JOIN_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.JOIN_GROUP_FAIL);
    }

    /**
     * 退出群组
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public UsersResponse signoutFromGroup(long userId, long groupId) {
        boolean isJoined = isJoined(groupId,userId);
        /**是否是该群组成员*/
        if (!isJoined)
            return new UsersResponse(MyEnum.NOT_IN_GROUP_ERROR);
        /**退出群组动作*/
        int resultCount = userGroupDao.deleteUserGroup(groupId,userId);
        if (resultCount>0){
            MyEnum.SIGNOUT_SUCCESS.setStateInfo("退出群"+groupId+"成功！");
        }else{
            MyEnum.SIGNOUT_GROUP_FAIL.setStateInfo("退出群"+groupId+"失败！");
        }
        return new UsersResponse(MyEnum.SIGNOUT_SUCCESS);
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
        if (userGroup == null || "".equals(userGroup))      //查询结果为空，说明用户还没有加入该群组
            return false;
        else
            return true;
    }

    /**
     * 查询某群组内的用户id
     * @param groupId
     * @return
     */
    @Override
    public List<Long> queryAllUserIdOfGroup(long groupId) {
        List<Long> userIds = userGroupDao.queryAllUserIdOfGroup(groupId);
        return userIds;
    }

    @Override
    public int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage, long userId, long groupId) {
        JsonFormat jsonFormat = new JsonFormat();       //maven中这个jar包没有导入成功!
        String json = jsonFormat.printToString(protoMessage);//将protoMessage转换为String
        String newOfflineText = userGroupDao.getOfflineTextUnique(userId,groupId) + json + ",";     //将之前的离线消息和新消息合并起来
        int result = userGroupDao.updateOfflineTextUnique(newOfflineText,userId,groupId);
        return result;
    }

    @Override
    public List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId) {
        JsonFormat jsonFormat = new JsonFormat();
        MessageProtoBuf.ProtoMessage.Builder builder = MessageProtoBuf.ProtoMessage.newBuilder();
        ExtensionRegistry registry = ExtensionRegistry.newInstance();;

        List<MessageProtoBuf.ProtoMessage> outPut = new ArrayList<>();
        List<String> jsonList = new ArrayList<>();
        List<String> results = userGroupDao.getOfflineTextMulti(userId);
        StringBuffer stringBuffer;  //拼接results数组为一个json格式。(这里必须用stringbuffer，因为直接在循环体内拼接的话，相当于每次都要new出一个新的stringbuffer，造成浪费！)
        if (results==null||results.isEmpty()||results.size()==0){
            return null;
        }else {
            stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            for (String s:results){
                stringBuffer.append(s);
            }
            stringBuffer.append("]");
        }
        JSONArray jsonArray = new JSONArray(stringBuffer.toString());
        for (Object j:jsonArray){
            jsonList.add(j.toString());
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


    /**
     * 将用户集合一个个的添加入某群组中
     * @param userIdSet
     * @param managerId
     * @param groupName
     * @param picId
     * @return
     */
    @Override
    public UsersResponse addUserToGroup(Set<Long> userIdSet, long managerId, String groupName, int picId)
    throws AddPicIdErrorException,AddUserToGroupErrorException{
        Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
        long groupId = group.getId();
        /**添加群组头像*/
        try {
            int isAdded = groupDao.addGroupPic(groupId,picId);
        }catch (Exception e){
            throw new AddPicIdErrorException("添加群组头像失败！");
        }
        int resultCount;
        Iterator<Long> iterator = userIdSet.iterator();
        /**将管理者选择的用户加入该群组*/
        while (iterator.hasNext()) {
            try {
                resultCount = userGroupDao.saveUserGroup(groupId, iterator.next());
            }catch (Exception e){
                throw new AddUserToGroupErrorException("用户加入群组出现错误！");
            }
            /**如果加入群组成功，将用户id从userIdSet中除去*/
//            iterator.remove();      //如果加入群组成功，将用户id从userIdSet中除去
        }
        return new UsersResponse(MyEnum.CREATE_GROUP_SUCCESS,groupName+"("+groupId+")");  //如果顺利执行到此处，则说明全部加入群组成功！
        /**如果set中仍有剩余，说明仍有部分用户没有加入成功*/
//        if (userIdSet.size()>0){
//            StringBuffer remainUsers = new StringBuffer();  //残余未成功添加的用户id，使用“，”隔开
//            for (long userId:userIdSet){
//                remainUsers.append(userId+",");
//            }
//            return new UsersResponse(MyEnum.REMAIN_USERS);
//        }else {
//            return new UsersResponse(MyEnum.CREATE_GROUP_SUCCESS);
//        }
    }

}
