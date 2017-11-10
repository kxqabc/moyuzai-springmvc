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


public abstract class MinaModel {

    protected MessageProtoBuf.ProtoMessage message;

    protected Map<Long,IoSession> sessionMap;

    protected Map<Long,Long> idMap;

    public MinaModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap) {
        this.message = message;
        this.sessionMap = sessionMap;
        this.idMap = idMap;
    }

    protected boolean isOnline(long userId){
        if (DataFormatTransformUtil.isNullOrEmpty(idMap))
            return false;
        if (idMap.containsKey(userId))
            return true;
        else
            return false;
    }

    protected abstract void handle() throws IoSessionIllegalException;

    protected IoSession getSessionByUserId(long userId) throws IoSessionIllegalException {
        if (DataFormatTransformUtil.isNullOrEmpty(sessionMap))
            throw new IoSessionIllegalException("MinaModel获取session时发生异常！");
        long sessionId = idMap.get(userId);
        if (DataFormatTransformUtil.isNullOrEmpty(sessionId))
            throw new IoSessionIllegalException("在session映射表中找不到对应userId的sessionId！");
        return sessionMap.get(sessionId);
    }

}
