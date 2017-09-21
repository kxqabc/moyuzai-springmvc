package com.moyuzai.servlet.service;

import com.google.gson.Gson;
import com.moyuzai.servlet.dao.GroupDao;
import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.mina.core.ServerHandler;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import com.sun.corba.se.impl.interceptors.PICurrent;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proto.MessageProtoBuf;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MinaServiceImpl implements MinaService{

    //增加本条注释并commit

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserGroupDao userGroupDao;

    @Resource
    private ServerHandler serverHandler;    //mina的核心处理器，这里controller需要控制mina主动发送消息

    @Override
    public boolean isOnline(long userId) {
        return serverHandler.getSessionMap().containsKey(userId);
    }

    /**
     * 在创建群组（带有初始化信息：组员、头像）成功后，依次通知所有被拉入该组的用户（用户在线则立即发送socket消息，
     * 若离线则讲信息保存在对应的数据库表中。）
     * @param userIdSet
     * @param usersResponse     从中获取groupId信息
     */
    @Override
    public void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,UsersResponse usersResponse) {
        String groupInfo = (String)usersResponse.getIdentity();     //从dto中取出群组信息：ID和名称
        long groupId = Long.parseLong(groupInfo.substring(groupInfo.indexOf("(") + 1, groupInfo.indexOf(")")));     //从群组信息中获取群组ID
        Group group = groupDao.queryGroupWithManNameById(groupId);
        int userAmount = userGroupDao.queryAmountInGroupByGroupId(groupId);   //获取群人数
        group.setAmount(userAmount);
        String groupJSON = new Gson().toJson(group);
        /** 使用工具类方法将“普通信息”包装成protoMessage */
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessage(
                MessageProtoBuf.ProtoMessage.Type.JOIN_GROUP_NOTIFY,    //构造ProtoMessage
                "","",groupJSON);
        /** 调用serverHandler的方法利用socket通知用户 */
        serverHandler.notifyAllUsers(userIdSet,groupId,message);        //通知被拉入的组员
    }

    @Override
    public void notifyUserGroupIsDisMissed(Set<Long> userIdSet,long groupId) {
        /** 使用工具类方法将“普通信息”包装成protoMessage */
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessage(
                MessageProtoBuf.ProtoMessage.Type.DISMISS_GROUP_NOTIFY,    //构造ProtoMessage
                "","",""+groupId);
        serverHandler.groupDismissNotify(userIdSet,groupId,message);
    }

    /**
     * 通知其他人群组资料变化
     * @param groupId
     * @param managerId
     * @param groupName
     * @param picId
     */
    @Override
    public void notifyUsersGroupMessageChange(long groupId,long managerId,String groupName,int picId) {
        List<Long> userIds = userGroupService.queryAllUserIdOfGroup(groupId);
        Set<Long> userIdSet = new HashSet<>();
        userIdSet.addAll(userIds);
        userIdSet.remove(managerId);       //不用通知自己
        if (userIdSet.isEmpty())
            return;
        //构造group对象
        int amount = userGroupDao.queryAmountInGroupByGroupId(groupId);
        Group group = groupDao.queryGroupWithManNameById(groupId);
        group.setAmount(amount);
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessage(
                MessageProtoBuf.ProtoMessage.Type.UPDATE_GROUP_NOTIFY,"","",new Gson().toJson(group));
        //通知
        serverHandler.notifyAllUsers(userIdSet,groupId,message);
    }

    /**
     * 通知群组的其他人，有人刚刚加入了群组
     * @param groupId
     * @param userId
     */
    @Override
    public void notifySomeJoined(long groupId, long userId) {
        groupMemberChange(groupId,userId, MessageProtoBuf.ProtoMessage.Type.SOMEONE_JOIN_NOTIFY);
    }

    @Override
    public void notifySomeOut(long groupId, long userId) {
        groupMemberChange(groupId,userId, MessageProtoBuf.ProtoMessage.Type.SOMEONE_QUIT_NOTIFY);
    }

    public void groupMemberChange(long groupId, long userId, MessageProtoBuf.ProtoMessage.Type type){
        //得到该群组的所有组员
        List<Long> userIds = userGroupService.queryAllUserIdOfGroup(groupId);
        Set<Long> userIdSet = new HashSet<>();
        userIdSet.addAll(userIds);
        userIdSet.remove(userId);       //不用通知自己
        if (userIdSet.isEmpty())
            return;
        int usersAmount = userGroupDao.queryAmountInGroupByGroupId(groupId);
        String userName = userDao.queryById(userId).getUserName();
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("userId",userId);
        jsonMap.put("userName",userName);
        jsonMap.put("groupId",groupId);
        jsonMap.put("amount",usersAmount);
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessage(
                type, "","",new Gson().toJson(jsonMap));
        //通知
        serverHandler.notifyAllUsers(userIdSet,groupId,message);
    }
}
