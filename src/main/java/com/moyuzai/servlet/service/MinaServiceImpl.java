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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proto.MessageProtoBuf;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MinaServiceImpl implements MinaService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

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
        notifyUserIsPulledIntoGroup(userIdSet,groupId);
    }
    @Override
    public void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,long groupId){
        Group group = groupDao.queryGroupWithManNameById(groupId);
        int userAmount = userGroupDao.queryAmountInGroupByGroupId(groupId);   //获取群人数
        group.setAmount(userAmount);
        String groupJSON = new Gson().toJson(group);
        /** 使用工具类方法将“普通信息”包装成protoMessage */
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.JOIN_GROUP_NOTIFY, groupJSON);
        /** 调用serverHandler的方法利用socket通知用户 */
        serverHandler.notifyAllUsers(userIdSet,groupId,message);        //通知被拉入的组员
    }

    @Override
    public void notifyUserGroupIsDisMissed(Set<Long> userIdSet,long groupId) {
        logger.info("通知所有成员群组已经解散！");
        logger.info("userIdSet"+userIdSet.size());
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))
            return;
        /** 使用工具类方法将“普通信息”包装成protoMessage */
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.DISMISS_GROUP_NOTIFY, ""+groupId);
        serverHandler.groupDismissNotify(userIdSet,groupId,message);
    }

    /**
     * 通知其他人群组资料变化
     */
    @Override
    public void notifyUsersGroupMessageChange(Set<Long> userSet,Group group,String addUsers) {
        if (DataFormatTransformUtil.isNullOrEmpty(userSet))
            return;
        userSet.remove(group.getManagerId());       //不用通知自己
        //构造group对象
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("id",group.getId());
        jsonMap.put("groupName",group.getGroupName());
        jsonMap.put("managerId",group.getManagerId());
        jsonMap.put("picId",group.getPicId());
        jsonMap.put("amount",group.getAmount());
        jsonMap.put("managerName",group.getManagerName());
        if (!DataFormatTransformUtil.isNullOrEmpty(addUsers)){
            Set<Long> addUserSet = DataFormatTransformUtil.StringToLongSet(addUsers);
            if (!DataFormatTransformUtil.isNullOrEmpty(addUserSet)){
                String addUsersName = userService.getUsersName(addUserSet);
                jsonMap.put("addUsers",addUsersName.substring(0,addUsersName.length()-1));
            }
        }else
            jsonMap.put("addUsers","");
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.UPDATE_GROUP_NOTIFY,new Gson().toJson(jsonMap));
        //通知
        serverHandler.notifyAllUsers(userSet,group.getId(),message);
    }

    /**
     * 通知群组的其他人，有人刚刚加入了群组
     * @param groupId
     * @param userId
     */
    @Override
    public void notifySomeJoined(long groupId, long userId) {
        //获取该群组的所有组员
        List<Long> userIds = userGroupService.queryAllUserIdOfGroup(groupId);
        Set<Long> userIdSet = new HashSet<>();
        userIdSet.addAll(userIds);
        userIdSet.remove(userId);       //不用通知自己
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))
            return;
        //获取群组中成员的数量
        int usersAmount = userGroupDao.queryAmountInGroupByGroupId(groupId);
        String userName = userDao.queryById(userId).getUserName();
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("userId",userId);
        jsonMap.put("userName",userName);
        jsonMap.put("groupId",groupId);
        jsonMap.put("amount",usersAmount);
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.SOMEONE_JOIN_NOTIFY,new Gson().toJson(jsonMap));
        //通知
        serverHandler.notifyAllUsers(userIdSet,groupId,message);
    }

    /**
     * 通知已经被踢出该群组
     * @param userIdSet
     * @param groupId
     */
    @Override
    public void notifyUsersIsKickout(Set<Long> userIdSet,long groupId) {
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.QUIT_GROUP_NOTIFY,""+groupId);
        serverHandler.groupDismissNotify(userIdSet,groupId,message);
    }


}
