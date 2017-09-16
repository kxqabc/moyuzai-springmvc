package com.moyuzai.servlet.mina.entity;

public class ChatMessage {

	private String body;
	private String from;
	private String to;
	private String type;

	public ChatMessage() {}

	public ChatMessage(String type, String from, String to, String body) {
		this.body = body;
		this.from = from;
		this.to = to;
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
