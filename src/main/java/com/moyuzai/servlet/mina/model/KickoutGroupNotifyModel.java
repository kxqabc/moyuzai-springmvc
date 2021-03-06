package com.moyuzai.servlet.mina.model;

import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import proto.MessageProtoBuf;

import java.util.Map;
import java.util.Set;

public class KickoutGroupNotifyModel extends GroupDissmissNotifyModel {

    public KickoutGroupNotifyModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
                                   UserGroupService userGroupService, Set<Long> userIdSet, Map<String, Object> paramterMap) {
        super(message, sessionMap, idMap, userGroupService, userIdSet, paramterMap);
    }

    @Override
    protected boolean packingProtoMessage() {
        groupId = (long) paramterMap.get("groupId");
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;
        message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.QUIT_GROUP_NOTIFY,""+groupId);
        return true;
    }
}
