package com.moyuzai.servlet.mina.model;

import java.util.List;
import java.util.Map;

import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;

/**
 * 将用户存入session列表
 */
public class ChatModel extends MinaModel{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private UserGroupService userGroupService;

	private IoSession mSession;

	private long fromId;

	private long groupId;

	public ChatModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
					 UserGroupService userGroupService, IoSession mSession) {
		super(message, sessionMap, idMap);
		this.userGroupService = userGroupService;
		this.mSession = mSession;
	}

	public boolean checkUp(){
		String from = message.getFrom();
		String to = message.getTo();
		fromId = Long.parseLong(from.substring(from.indexOf("(") + 1, from.indexOf(")")));
		groupId = Long.parseLong(to);
		/**首先验证用户是不是在该组内*/
		boolean isMemberOfGroup = userGroupService.isJoined(groupId,fromId);
		if (!isMemberOfGroup){
			return false;
		}
		return true;
	}

	public void handle() throws IoSessionIllegalException {
		MessageProtoBuf.ProtoMessage chatResponse;
		//检查用户是不是在该组内
		boolean isAccess = checkUp();
		String sendTime = message.getTime();	//获取信息发送时刻，作标识符
		if (!isAccess){		//没有通过检验
			logger.info("发送群聊消息检验：群组ID错误或用户不在该群组中！");
			//服务器聊天核对失败回执
			chatResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.CHAT_RESPONSE, sendTime,"error");
			mSession.write(chatResponse);
		}else {				//成功通过检验
			//服务器聊天核对成功回执
			chatResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.CHAT_RESPONSE,sendTime, "ok");
			mSession.write(chatResponse);
			ServiceData serviceData = userGroupService.queryAllUserIdOfGroup(groupId);
			List<Long> userIdList;
			if (serviceData.isState()){
				userIdList = (List<Long>) serviceData.getData();
			}else {
				return;
			}
			//将信息发送者从发送名单列表中除去
			if (userIdList.contains(fromId))
				userIdList.remove(fromId);
			//推送信息：两种方式
			if (!DataFormatTransformUtil.isNullOrEmpty(userIdList)) {
				for (long userId : userIdList) {
					if(isOnline(userId)){
						//用户在线，马上推送
						getSessionByUserId(userId).write(message);
						logger.info("用户："+userId+"在线，推送消息："+message.getBody());
					}else{
						//用户不在线
						userGroupService.insertOfflineText(message, userId, groupId);
						logger.info("用户："+userId+"不在线，插入离线信息:"+message.getBody());
					}
				}
			}
		}
	}

}
