package com.moyuzai.servlet.mina.core;

import java.util.*;

import com.googlecode.protobuf.format.JsonFormat;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.mina.model.ChatModel;
import com.moyuzai.servlet.mina.model.LoginModel;
import com.moyuzai.servlet.mina.model.NotifyModel;
import com.moyuzai.servlet.mina.model.NotifyUser;
import com.moyuzai.servlet.service.GroupService;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;
import proto.MessageProtoBuf.ProtoMessage.Type;


public class ServerHandler extends IoHandlerAdapter {

	private Log log = LogFactory.getLog(ServerHandler.class);

	@Autowired
	private UserService userService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private UserGroupDao userGroupDao;

	private Map<Long,Long> sessionMap = new HashMap<>();	//1:userId, 2:sessionId

	public ServerHandler() {
		log.info("ServerHandler启动..");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.info("exceptionCaught: " + cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageProtoBuf.ProtoMessage protoMessage = (MessageProtoBuf.ProtoMessage) message;
		log.info("protobufTime:"+protoMessage.getTime());
		log.info("recieve:" + protoMessage);
		Type type = protoMessage.getType();
		if(type == Type.CHAT){
			//聊天
			ChatModel chatModel = new ChatModel(sessionMap, session, protoMessage);
			chatModel.handle();
		}else if(type == Type.LOGIN){
			//登录
			LoginModel loginModel = new LoginModel(sessionMap, session, protoMessage);
			loginModel.handle();
		}else if (type == Type.HEART_BEAT){
			session.write(DataFormatTransformUtil.packingToProtoMessageOption(Type.HEART_BEAT_RESPONSE,"ok"));
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed:"+session.toString());
		long sessionId = session.getId();
		long userId  = (long) session.getAttribute("userId");
		//如果没有“userId”属性，无法根据map的key删除一个键值对，只能通过value“sessionId”去删除
		if (DataFormatTransformUtil.isNullOrEmpty(userId)){
			if(sessionMap.containsValue(sessionId)){
				Collection<Long> c = sessionMap.values();
				c.remove(sessionId);
			}
		}else {	//session中存在“userId”属性，通过key"userId"删除键值对
			if (sessionMap.containsKey(userId)){
				sessionMap.remove(userId);
			}
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpen");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}

	/**
	 * 内部类，负责通知用户
	 */
	public class Notify{

		public NotifyUser notifyUser;

		public Notify(NotifyUser notifyUser) {
			this.notifyUser = notifyUser;
		}

		public void notifyUser() throws IoSessionIllegalException {
			notifyUser.notifyUsers();
		}
	}

	public Map<Long, Long> getSessionMap() {
		return sessionMap;
	}

	@Override
	public String toString() {
		return super.toString() + "\n"+ "mapSize:"+this.sessionMap.size();
	}
}
