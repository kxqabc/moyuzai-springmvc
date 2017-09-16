package com.moyuzai.servlet.mina.util;

import com.moyuzai.servlet.mina.entity.ChatMessage;
import org.json.JSONObject;

public class MessageParser {
	public static ChatMessage parse(String message) {
		ChatMessage chatMessage = null;
		JSONObject jsonObject =new JSONObject(message);
		String type;
		String from;
		String to;
		String body;
		if(jsonObject.has("type")){
			type = jsonObject.getString("type");
		}else{
			type = null;
		}
		if(jsonObject.has("from")){
			from = jsonObject.getString("from");
		}else{
			from = null;
		}
		if(jsonObject.has("to")){
			to = jsonObject.getString("to");
		}else{
			to = null;
		}
		if(jsonObject.has("body")){
			body = jsonObject.getString("body");
		}else{
			body = null;
		}
		chatMessage = new ChatMessage(type, from, to, body);
		return chatMessage;
	}
}
