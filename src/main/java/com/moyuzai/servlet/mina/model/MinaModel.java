package com.moyuzai.servlet.mina.model;

import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.GroupService;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import proto.MessageProtoBuf;

import java.util.Map;

@Component
public abstract class MinaModel {

    protected MessageProtoBuf.ProtoMessage message;

    protected IoSession session;

    protected Map<Long,Long> sessionMap;

    public MinaModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap) {
        this.message = message;
        this.session = session;
        this.sessionMap = sessionMap;
    }

    protected abstract void handle() throws IoSessionIllegalException;

    protected Map<Long,IoSession> getSessions(IoSession session) throws IoSessionIllegalException {
        if (DataFormatTransformUtil.isNullOrEmpty(session))
            throw new IoSessionIllegalException("session为空！");
        Map<Long,IoSession> sessions = session.getService().getManagedSessions();
        return sessions;
    }

    protected IoSession getSessionByUserId(long userId,IoSession session) throws IoSessionIllegalException {
        Map<Long,IoSession> sessions = getSessions(session);
        long sessionId = sessionMap.get(userId);
        if (DataFormatTransformUtil.isNullOrEmpty(sessionId))
            throw new IoSessionIllegalException("在session映射表中找不到对应userId的sessionId！");
        return sessions.get(sessionId);
    }

}
