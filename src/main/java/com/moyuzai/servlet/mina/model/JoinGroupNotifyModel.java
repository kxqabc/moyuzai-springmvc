package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JoinGroupNotifyModel extends NotifyModel implements NotifyUser{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private long groupId;

    public JoinGroupNotifyModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap,
                                Set<Long> userIds, Map<String, Object> paramter) {
        super(message, session, sessionMap, userIds, paramter);
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

        int usersAmount = (int) paramterMap.get("usersAmount");
        if (DataFormatTransformUtil.isNullOrEmpty(usersAmount))
            return false;

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
            notifyAllUsers(userIdSet,groupId,message);
        }
    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }

}
