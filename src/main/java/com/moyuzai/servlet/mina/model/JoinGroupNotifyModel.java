package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.GroupService;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JoinGroupNotifyModel extends NotifyModel implements NotifyUser{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private UserService userService;

    private long groupId;

    public JoinGroupNotifyModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
                                UserGroupService userGroupService, Set<Long> userIdSet, Map<String, Object> paramterMap, UserService userService) {
        super(message, sessionMap, idMap, userGroupService, userIdSet, paramterMap);
        this.userService = userService;
    }

    @Override
    protected boolean packingProtoMessage() {

        long userId = (long) paramterMap.get("userId");
        if (DataFormatTransformUtil.isNullOrEmpty(userId))
            return false;

        groupId = (long) paramterMap.get("groupId");
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;

        //查询新加入成员姓名
        ServiceData userData = userService.getUserById(userId);
        String userName;
        if (userData.isState()){
            userName = ((User)userData.getData()).getUserName();
        }else
            return false;
        //查询群组内组员数量
        int usersAmount = userGroupService.getAmountInGroupById(groupId);
        //将信息转换为json
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("userId",userId);
        jsonMap.put("userName",userName);
        jsonMap.put("groupId",groupId);
        jsonMap.put("amount",usersAmount);
        message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.SOMEONE_JOIN_NOTIFY,new Gson().toJson(jsonMap));
        return true;
    }

    @Override
    protected void handle() throws IoSessionIllegalException {
        boolean targetExist = checkoutTarget();
        if (targetExist){
            boolean isPackingSuccess = packingProtoMessage();
            if (!isPackingSuccess)
                return;
            notifyAllUsers(userIdSet,groupId,message,userGroupService);
        }
    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }

}
