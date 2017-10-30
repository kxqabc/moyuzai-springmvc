package com.moyuzai.servlet.mina.model;

import java.util.List;
import java.util.Map;

import com.moyuzai.servlet.service.GroupService;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

/**
 * 将用户存入session列表
 */
public class ChatModel {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Long, IoSession> mSessionMap;
	private IoSession mSession;
	private MessageProtoBuf.ProtoMessage mProtoMessage;
	private long fromId;
	private long groupId;

	public ChatModel(Map<Long, IoSession> map, IoSession session, MessageProtoBuf.ProtoMessage protoMessage) {
		mSessionMap = map;
		mSession = session;
		mProtoMessage = protoMessage;
	}

	public boolean checkUp(UserGroupService userGroupService){
		String from = mProtoMessage.getFrom();
		String to = mProtoMessage.getTo();
		fromId = Long.parseLong(from.substring(from.indexOf("(") + 1, from.indexOf(")")));
		groupId = Long.parseLong(to);
		/**首先验证用户是不是在该组内*/
//		boolean isGroupExist = groupService.checkGroupIsExist(groupId);
		boolean isMemberOfGroup = userGroupService.isJoined(groupId,fromId);
		if (!isMemberOfGroup){
			return false;
		}
		return true;
	}

	public void handle(UserGroupService userGroupService) {
		MessageProtoBuf.ProtoMessage chatResponse;
		//检查用户是不是在该组内
		boolean isAccess = checkUp(userGroupService);
		String sendTime = mProtoMessage.getTime();		//获取信息发送时刻，作标识符
		if (!isAccess){		//没有通过检验
			logger.info("发送群聊消息检验：群组ID错误或用户不在该群组中！");
			//服务器聊天核对失败回执
			chatResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.CHAT_RESPONSE, sendTime,"error");
			mSession.write(chatResponse);
		}else {				//成功通过检验
			//服务器聊天核对成功回执
			chatResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.CHAT_RESPONSE,sendTime, "ok");
			mSession.write(chatResponse);


			List<Long> userIdList = userGroupService.queryAllUserIdOfGroup(groupId);
			//将信息发送者从发送名单列表中除去
			Long aLong = new Long(fromId);
			userIdList.remove(aLong);
			//推送信息：两种方式
			if (userIdList != null && userIdList.size() > 0) {
				for (long userId : userIdList) {
					logger.info("userId="+userId);
					if(mSessionMap.containsKey(userId)){
						//用户在线，马上推送
						WriteFuture writeFuture = mSessionMap.get(userId).write(mProtoMessage);
						if (writeFuture.isWritten())
							System.out.println("user:"+userId+"isWriteen");
						else
							System.out.println("user:"+userId+"is not Writeen");
						logger.info("用户："+userId+"在线，推送消息："+mProtoMessage.getBody());
					}else{
						//用户不在线
						logger.info("插入离线信息:"+mProtoMessage);
						userGroupService.insertOfflineText(mProtoMessage, userId, groupId);
					}
				}
			}
		}
	}

}
