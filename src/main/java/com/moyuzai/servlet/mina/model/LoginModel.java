package com.moyuzai.servlet.mina.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

/**
 * 将用户存入session列表
 */
public class LoginModel {

	private static final Logger logger = LoggerFactory.getLogger(LoginModel.class);

	private Map<Long, IoSession> mSessionMap;;

	private IoSession mSession;

	private MessageProtoBuf.ProtoMessage mProtoMessage;

	public LoginModel(Map<Long, IoSession> map, IoSession session, MessageProtoBuf.ProtoMessage protoMessage) {
		mSessionMap = map;
		mSession = session;
		mProtoMessage = protoMessage;
	}

	public void handle(UserService userService, UserGroupService userGroupService) {
		logger.info("进入login.handle..");
		//登录回执
		MessageProtoBuf.ProtoMessage loginResponse;

		String from = mProtoMessage.getFrom();
		long userId = Long.parseLong(from.substring(from.indexOf("(") + 1, from.indexOf(")")));
		logger.info("from:"+from+",userId:"+userId);
		if (!userService.isUserExist(userId)){		//用户信息不正确
			//不正常的登录回执
			loginResponse = DataFormatTransformUtil.packingToProtoMessage(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE,
					"server","","error");
			mSession.write(loginResponse);
			logger.info("mina登录失败！");
		}else {					//用户信息正确，正常登录
			mSessionMap.put(userId, mSession);
			logger.info("在线用户数量="+mSessionMap.size());
			//正常的登录回执
			loginResponse = DataFormatTransformUtil.packingToProtoMessage(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE,
					"server","","ok");
			WriteFuture writeFuture = mSession.write(DataFormatTransformUtil.packingToProtoMessage(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE,
					"server","","ok"));
			//查询离线信息
			List<MessageProtoBuf.ProtoMessage> offlineTextList = userGroupService.getOfflineText(userId);
			if (offlineTextList == null||offlineTextList.equals("")||offlineTextList.isEmpty()) {
				logger.info("用户:" + userId + "没有离线信息");
			} else {
				logger.info("存在未推送信息！offlineTextList.size="+offlineTextList.size());
				for (MessageProtoBuf.ProtoMessage protoMessage: offlineTextList) {
					mSession.write(protoMessage);
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
