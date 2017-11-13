package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;
import sun.plugin2.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SomeoneQuitNotifyModel extends NotifyModel implements NotifyUser {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private MessageProtoBuf.ProtoMessage toUsers;

    private MessageProtoBuf.ProtoMessage toManager;

    private long groupId;

    private long managerId;

    public SomeoneQuitNotifyModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
                                  UserGroupService userGroupService, Set<Long> userIdSet, Map<String, Object> paramterMap) {
        super(message, sessionMap, idMap, userGroupService, userIdSet, paramterMap);
    }

    @Override
    protected boolean packingProtoMessage() {
        long userId = (long) paramterMap.get("userId");
        if (DataFormatTransformUtil.isNullOrEmpty(userId))
            return false;

        String userName = (String) paramterMap.get("userName");
        if (DataFormatTransformUtil.isNullOrEmpty(userName))
            return false;

        groupId = (long) paramterMap.get("groupId");
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;

        managerId = (long) paramterMap.get("managerId");
        if (DataFormatTransformUtil.isNullOrEmpty(managerId))
            return false;

        int amount = (int) paramterMap.get("amount");
        if (DataFormatTransformUtil.isNullOrEmpty(amount))
            return false;

        Map<String,Object> toUsersJson = new HashMap<>();
        Map<String,Object> toManagerJson = new HashMap<>();
        toUsersJson.put("groupId",groupId);
        toUsersJson.put("amount",amount);
        toManagerJson.put("groupId",groupId);
        toManagerJson.put("userId",userId);
        toManagerJson.put("userName",userName);

        toUsers = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.SOMEONE_QUIT_NOTIFY,new Gson().toJson(toUsersJson));
        toManager = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.SOMEONE_QUIT_NOTIFY,new Gson().toJson(toManagerJson));
        return true;
    }

    @Override
    protected void handle() throws IoSessionIllegalException {
        boolean targetExist = checkoutTarget();
        if (targetExist){
            boolean isPackingSuccess = packingProtoMessage();
            logger.info("组装通知信息成功？"+isPackingSuccess);
            if (!isPackingSuccess)
                return;
            //向管理员发送通知
            notifyUser(managerId,groupId,toManager,userGroupService);
            //向组员发送通知
            notifyAllUsers(userIdSet,groupId,toUsers,userGroupService);
        }

    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }
}
