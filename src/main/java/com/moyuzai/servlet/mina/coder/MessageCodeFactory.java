package com.moyuzai.servlet.mina.coder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;

import proto.MessageProtoBuf.ProtoMessage;

public class MessageCodeFactory implements ProtocolCodecFactory {

	private MyEncoder mEncoder;
	private MyDecoder mDecoder;

	public MessageCodeFactory() {
		mEncoder = new MyEncoder();
		mDecoder = new MyDecoder();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return mEncoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return mDecoder;
	}

	class MyEncoder implements ProtocolEncoder {
		@Override
		public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
			ProtoMessage protoMessage = (ProtoMessage) message;
			System.out.println("encode:"+((ProtoMessage) message).getBody());
			byte[] bytes = protoMessage.toByteArray();
			IoBuffer ioBuffer = IoBuffer.allocate(bytes.length);
			ioBuffer.setAutoExpand(true);
			ioBuffer.put(bytes);
			ioBuffer.flip();
			out.write(ioBuffer);
			System.out.println("encode:after send");
		}

		@Override
		public void dispose(IoSession session) throws Exception {

		}

	}

	class MyDecoder extends CumulativeProtocolDecoder {
		@Override
		protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			byte[] bytes = new byte[in.limit()];
			in.get(bytes);
			ProtoMessage protoMessage = ProtoMessage.parseFrom(bytes);
			out.write(protoMessage);
			return true;
		}

		@Override
		public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {

		}

		@Override
		public void dispose(IoSession session) throws Exception {

		}

	}
}
