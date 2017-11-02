package com.moyuzai.servlet.mina.model;

import com.googlecode.protobuf.format.JsonFormat;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

import java.util.Map;
import java.util.Set;

public abstract class NotifyModel extends MinaModel {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Set<Long> userIdSet;

    protected Map<String,Object> paramterMap;


    public NotifyModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap,
                       Set<Long> userIds,Map<String,Object> paramter) {
        super(message, session, sessionMap);
        userIdSet = userIds;
        paramterMap = paramter;
    }

    abstract protected boolean packingProtoMessage();

    protected boolean checkoutTarget(){
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))
            return false;
        if (DataFormatTransformUtil.isNullOrEmpty(paramterMap))
            return false;
        return true;
    }

    protected void notifyAllUsers(Set<Long> userIds,
                                  long groupId,
                                  MessageProtoBuf.ProtoMessage protoMessage) throws IoSessionIllegalException {
        if (userIds.isEmpty())
            return;
        for (long userId:userIds){
            if (sessionMap.containsKey(userId)){	//表示在线
                logger.info("用户："+userId+"在线,立即推送。。");
                getSessionByUserId(userId,session).write(protoMessage);     //依次发送通知
            }else {	//不在线，则保存在离线信息中
                logger.info("用户："+userId+"离线，将推送信息保存在数据库中。。");
                userGroupService.insertOfflineText(protoMessage,userId,groupId);
            }
        }
    }

    /**
     * 通知被解散群组中的所有成员
     * @param userIdSet
     * @param groupId
     * @param protoMessage
     */
    public void groupDismissNotify(Set<Long> userIdSet,long groupId,MessageProtoBuf.ProtoMessage protoMessage)
            throws IoSessionIllegalException {
        for (long userId:userIdSet){
            if (sessionMap.containsKey(userId)){	//表示在线
                logger.info("用户："+userId+"在线,立即推送。。");
                getSessionByUserId(userId,session).write(protoMessage);     //依次发送通知
            }else {	//不在线，则保存在离线信息中
                logger.info("用户："+userId+"离线，将推送信息保存在数据库中。。");
                ServiceData serviceData = userGroupService.queryAnotherGroupOfUser(groupId,userId);
                UserGroup insertedGroup = null;
                if (serviceData.isState()){//追加信息
                    insertedGroup = (UserGroup) serviceData.getData();
                    userGroupService.insertOfflineText(protoMessage,userId,insertedGroup.getGroupId());
                }else {//表示该用户已经没有其他群组了

                }
            }
        }
    }

}
