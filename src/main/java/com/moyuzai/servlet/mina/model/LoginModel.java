package com.moyuzai.servlet.mina.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

/**
 * 将用户存入session列表
 */
public class LoginModel extends MinaModel{

	private static final Logger logger = LoggerFactory.getLogger(LoginModel.class);

	public LoginModel(Map<Long, Long> map, IoSession session, MessageProtoBuf.ProtoMessage protoMessage) {
		super(protoMessage,session,map);
	}

	public void handle() {
		logger.info("进入login.handle..");
		//登录回执
		MessageProtoBuf.ProtoMessage loginResponse;
		String from = message.getFrom();
		long userId = Long.parseLong(from.substring(from.indexOf("(") + 1, from.indexOf(")")));
		logger.info("from:"+from+",userId:"+userId);
		if (!userService.isUserExist(userId)){		//用户信息不正确
			//不正常的登录回执
			loginResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE, "error");
			session.write(loginResponse);
			logger.info("mina登录失败！");
		}else {					//用户信息正确，正常登录
			//将userId-sessionId保存到映射表中
			sessionMap.put(userId, session.getId());
			//将userId添加到session的属性中
			session.setAttribute("userId",userId);
			logger.info("在线用户数量="+sessionMap.size());
			//正常的登录回执
			loginResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE,"ok");
			session.write(loginResponse);
			//查询离线信息
			List<MessageProtoBuf.ProtoMessage> offlineTextList = userGroupService.getOfflineText(userId);
			if (offlineTextList == null||offlineTextList.equals("")||offlineTextList.isEmpty()) {
				logger.info("用户:" + userId + "没有离线信息");
				//查询下用户是不是一个群组都没有了
				int groupAmount = userGroupService.getGroupAmountByUserId(userId);
				if (groupAmount==0){
					//通知用户一个群组也没有了
					MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
							MessageProtoBuf.ProtoMessage.Type.NO_GROUP_NOTIFY,"ok");
					session.write(message);
				}
			} else {
				logger.info("存在未推送信息！offlineTextList.size="+offlineTextList.size());
				for (MessageProtoBuf.ProtoMessage protoMessage: offlineTextList) {
					session.write(protoMessage);
					logger.info("离线推送消息，发给用户:" + userId + ",内容为: ");
					logger.info(protoMessage.toString());
				}
			}
		}



	}

	/**
	 * 在被“拉入”群组时，立即通知所有在线的用户，不在线的用户则保存在数据库中。
	 * @param userIds
	 * @param groupId
	 * @param sessionMap
	 * @param protoMessage
	 * @param userGroupService
	 */
	public void notifyUserIsPulledIntoGroup(Set<Long> userIds,
											long groupId,
											Map<Long, IoSession> sessionMap,
											MessageProtoBuf.ProtoMessage protoMessage,
											UserGroupService userGroupService){
		for (long userId:userIds){
			if (sessionMap.containsKey(userId)){	//表示在线
				((IoSession)sessionMap.get(userId)).write(protoMessage);	//依次发送通知
			}else {		//不在线，则保存在离线信息中
				userGroupService.insertOfflineText(protoMessage,userId,groupId);
			}
		}
	}
}
