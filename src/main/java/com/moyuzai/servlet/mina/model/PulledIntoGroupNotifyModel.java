package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

import java.util.Map;
import java.util.Set;

/**
 * 要求详细的Group资料（包含群组管理员名称和组员数量）
 */
public class PulledIntoGroupNotifyModel extends NotifyModel implements NotifyUser{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private long groupId;

    public PulledIntoGroupNotifyModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap,
                                      Set<Long> userIds,Map<String,Object> paramter) {
        super(message, session, sessionMap,userIds,paramter);
    }

    @Override
    protected boolean packingProtoMessage() {
        Group group = (Group) paramterMap.get("group");
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return false;
        groupId = group.getId();
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;
        String groupJSON = new Gson().toJson(group);
        // 使用工具类方法将“普通信息”包装成protoMessage
        MessageProtoBuf.ProtoMessage protoMessage = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.JOIN_GROUP_NOTIFY, groupJSON);
        message = protoMessage;
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
