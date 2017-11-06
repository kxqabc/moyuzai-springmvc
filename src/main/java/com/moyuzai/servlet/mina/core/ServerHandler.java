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
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;
import proto.MessageProtoBuf.ProtoMessage.Type;


public class ServerHandler extends IoHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;
	@Autowired
	private UserGroupService userGroupService;

	private boolean isBuilt = false;
	//包含ioSession的map，是mina自己维护的sessionMap，通过session.getService().getManagedSessions()获得
	private Map<Long,IoSession> ioSessionMap;

	private Map<Long,Long> idMap = new HashMap<>();	//1:userId, 2:sessionId

	public ServerHandler() {
		logger.info("ServerHandler启动..");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.info("exceptionCaught: " + cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageProtoBuf.ProtoMessage protoMessage = (MessageProtoBuf.ProtoMessage) message;
		logger.info("protobufTime:"+protoMessage.getTime());
		logger.info("recieve:" + protoMessage);
		Type type = protoMessage.getType();
		if(type == Type.CHAT){
			//聊天
			ChatModel chatModel = new ChatModel(protoMessage,ioSessionMap,idMap,userGroupService,session);
			chatModel.handle();
		}else if(type == Type.LOGIN){
			//登录
			LoginModel loginModel = new LoginModel(protoMessage,ioSessionMap,idMap,userService,userGroupService,session);
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
		logger.info("ioSessionMap.size:"+ioSessionMap.size());
		long sessionId = session.getId();
		long userId  = (long) session.getAttribute("userId");
		//如果没有“userId”属性，无法根据map的key删除一个键值对，只能通过value“sessionId”去删除
		if (DataFormatTransformUtil.isNullOrEmpty(userId)){
			if(idMap.containsValue(sessionId)){
				Collection<Long> c = idMap.values();
				c.remove(sessionId);
			}
		}else {	//session中存在“userId”属性，通过key"userId"删除键值对
			if (idMap.containsKey(userId)){
				idMap.remove(userId);
			}
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpen");
		if (!this.isBuilt)
			builtIoSesionMap(session);
		logger.info("ioSessionMap.size:"+ioSessionMap.size());
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

	private Map<Long,IoSession> builtIoSesionMap(IoSession ioSession) throws IoSessionIllegalException {
		if (DataFormatTransformUtil.isNullOrEmpty(ioSession))
			throw new IoSessionIllegalException("构造IoSesionMap时发生异常：session为null!");
		ioSessionMap = ioSession.getService().getManagedSessions();
		if (!DataFormatTransformUtil.isNullOrEmpty(ioSessionMap))
			this.isBuilt = true;
		return ioSessionMap;
	}

	public Map<Long, IoSession> getIoSessionMap() {
		return ioSessionMap;
	}

	public Map<Long, Long> getIdMap() {
		return idMap;
	}

	@Override
	public String toString() {
		return super.toString() + "\n"+ "mapSize:"+this.idMap.size();
	}
}
