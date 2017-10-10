package com.moyuzai.servlet.mina.core;

import java.util.*;

import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.mina.model.ChatModel;
import com.moyuzai.servlet.mina.model.LoginModel;
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

	private Map<Long,IoSession> sessionMap = new HashMap<>();

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
		log.info("recieve:" + protoMessage);
		Type type = protoMessage.getType();
		if(type == Type.CHAT){
			//聊天
			ChatModel chatModel = new ChatModel(sessionMap, session, protoMessage);
			chatModel.handle(userGroupService);
		}else if(type == Type.LOGIN){
			//登录
			LoginModel loginModel = new LoginModel(sessionMap, session, protoMessage);
			loginModel.handle(userService,userGroupService);
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
		if(sessionMap.containsValue(session)){
			Collection<IoSession> c = sessionMap.values();
			c.remove(session);
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
	 * 在被“拉入”群组时，立即通知所有在线的用户，不在线的用户则保存在数据库中。
	 * @param userIds
	 * @param groupId
	 * @param protoMessage
	 */
	public void notifyAllUsers(Set<Long> userIds,
									  long groupId,
									  MessageProtoBuf.ProtoMessage protoMessage){
		log.info("开始通知所有组员已经被拉入该群组, "+"users="+userIds.toString());
		if (userIds.isEmpty())
			return;
		for (long userId:userIds){
			if (sessionMap.containsKey(userId)){	//表示在线
				log.info("用户："+userId+"在线,立即推送。。");
				((IoSession)sessionMap.get(userId)).write(protoMessage);	//依次发送通知
			}else {		//不在线，则保存在离线信息中
				log.info("用户："+userId+"离线，将推送信息保存在数据库中。。");
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
	public void groupDismissNotify(Set<Long> userIdSet,long groupId,MessageProtoBuf.ProtoMessage protoMessage){
		for (long userId:userIdSet){
			if (sessionMap.containsKey(userId)){	//表示在线
				log.info("用户："+userId+"在线,立即推送。。");
				((IoSession)sessionMap.get(userId)).write(protoMessage);	//依次发送通知
			}else {		//不在线，则保存在离线信息中
				log.info("用户："+userId+"离线，将推送信息保存在数据库中。。");
				UserGroup insertedGroup = userGroupDao.queryAnotherGroupOfUser(groupId,userId);
				if (DataFormatTransformUtil.isNullOrEmpty(insertedGroup)){		//表示该用户已经没有其他群组了

				}else {
					//获取之前的离线信息
					List<MessageProtoBuf.ProtoMessage> oldMessageList = userGroupService.getOfflineText(userId);
					//如果没有其他离线消息，就只保存这条
					if (oldMessageList == null || "".equals(oldMessageList) || oldMessageList.size()==0){
						userGroupService.insertOfflineText(protoMessage,userId,insertedGroup.getGroupId());
					}else {
						oldMessageList.add(protoMessage);
						//将解散消息存放在该用户的其他群组的离线信息中，考虑之前该组的离线信息
						for (MessageProtoBuf.ProtoMessage message:oldMessageList){
							userGroupService.insertOfflineText(message,userId,insertedGroup.getGroupId());
						}
					}
				}
			}
		}
	}


	public Map<Long, IoSession> getSessionMap() {
		return sessionMap;
	}

	@Override
	public String toString() {
		return super.toString() + "\n"+ "mapSize:"+this.sessionMap.size();
	}
}
