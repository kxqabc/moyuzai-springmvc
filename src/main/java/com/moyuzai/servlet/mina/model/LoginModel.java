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
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;

/**
 * 将用户存入session列表
 */
public class LoginModel extends MinaModel{

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private UserService userService;

	private UserGroupService userGroupService;

	private IoSession mSession;

	public LoginModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
					  UserService userService, UserGroupService userGroupService, IoSession mSession) {
		super(message, sessionMap, idMap);
		this.userService = userService;
		this.userGroupService = userGroupService;
		this.mSession = mSession;
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
			mSession.write(loginResponse);
			logger.info("mina登录失败！");
		}else {					//用户信息正确，正常登录
			//将userId-sessionId保存到映射表中
			idMap.put(userId, mSession.getId());
			//将userId添加到session的属性中
			mSession.setAttribute("userId",userId);
			logger.info("在线用户数量="+idMap.size());
			//正常的登录回执
			loginResponse = DataFormatTransformUtil.packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type.LOGIN_RESPONSE,"ok");
			mSession.write(loginResponse);
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
					mSession.write(message);
				}
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

}
