package com.moyuzai.servlet.mina.model;

import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;

import java.util.Map;
import java.util.Set;

public class GroupDissmissNotifyModel extends NotifyModel implements NotifyUser {

    protected long groupId;

    public GroupDissmissNotifyModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap,
                                    UserGroupService userGroupService, Set<Long> userIdSet, Map<String, Object> paramterMap) {
        super(message, session, sessionMap, userGroupService, userIdSet, paramterMap);
    }

    @Override
    protected boolean packingProtoMessage() {
        groupId = (long) paramterMap.get("groupId");
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;
        message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.DISMISS_GROUP_NOTIFY, ""+groupId);
        return true;
    }

    @Override
    protected void handle() throws IoSessionIllegalException {
        boolean targetExist = checkoutTarget();
        if (targetExist){
            boolean isPackingSuccess = packingProtoMessage();
            if (!isPackingSuccess)
                return;
            groupDismissNotify(userIdSet,groupId,message,userGroupService);
        }
    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }
}
