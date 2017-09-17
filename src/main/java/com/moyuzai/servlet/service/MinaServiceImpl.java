package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.mina.core.ServerHandler;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proto.MessageProtoBuf;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class MinaServiceImpl implements MinaService{

    //增加本条注释并commit

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;

    @Resource
    private ServerHandler serverHandler;    //mina的核心处理器，这里controller需要控制mina主动发送消息

    /**
     * 在创建群组（带有初始化信息：组员、头像）成功后，依次通知所有被拉入该组的用户（用户在线则立即发送socket消息，
     * 若离线则讲信息保存在对应的数据库表中。）
     * @param userIdSet
     * @param groupName
     * @param usersResponse     从中获取groupId信息
     */
    @Override
    public void notifyUserIsPulledIntoGroup(Set<Long> userIdSet,String groupName,UsersResponse usersResponse) {
        String groupInfo = (String)usersResponse.getIdentity();     //从dto中取出群组信息：ID和名称
        long groupId = Long.parseLong(groupInfo.substring(groupInfo.indexOf("(") + 1, groupInfo.indexOf(")")));     //从群组信息中获取群组ID
        /** 使用工具类方法将“普通信息”包装成protoMessage */
        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessage(MessageProtoBuf.ProtoMessage.Type.CHAT,    //构造ProtoMessage
                ""+groupId,"","你已经被拉入群组：" + groupName + "(" + groupId + ")");
        /** 调用serverHandler的方法利用socket通知用户 */
        serverHandler.pulledIntoGroupNotify(userIdSet,groupId,message,userGroupService);        //通知被拉入的组员
    }
}
