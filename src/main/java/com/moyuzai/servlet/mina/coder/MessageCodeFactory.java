package com.moyuzai.servlet.mina.coder;

import com.moyuzai.servlet.util.DataFormatTransformUtil;
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
			byte[] bytes = protoMessage.toByteArray();
			System.out.println("encoderLog:"+ DataFormatTransformUtil.bytesToHexString(bytes));
			int length = bytes.length;
			IoBuffer ioBuffer = IoBuffer.allocate(length+4);
			ioBuffer.setAutoExpand(true);
			ioBuffer.putInt(length);
			ioBuffer.put(bytes);
			ioBuffer.flip();
			out.write(ioBuffer);
//			System.out.println("encoderLog:"+((ProtoMessage)message).toByteArray());
//			out.write(message);
		}

		@Override
		public void dispose(IoSession session) throws Exception {

		}

	}

	class MyDecoder extends CumulativeProtocolDecoder {
		@Override
		protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

			// 如果没有接收完Header部分（4字节），直接返回false
			if (in.remaining() < 4) {
				return false;
			} else {

				// 标记开始位置，如果一条消息没传输完成则返回到这个位置
				in.mark();

				// 读取header部分，获取body长度(长度单位：字节数)
				int bodyLength = in.getInt();
//				System.out.println("decoderLog:"+);
				// 如果body没有接收完整，直接返回false
				if (in.remaining() < bodyLength) {
					in.reset(); // IoBuffer position回到原来标记的地方
					return false;
				} else {
//					byte[] allBytes = new byte[bodyLength+4];
//					in.get(allBytes);
//					System.out.println("decoderLog:"+DataFormatTransformUtil.bytesToHexString(allBytes));
					byte[] bodyBytes = new byte[bodyLength];
					in.get(bodyBytes); // 读取body部分
					System.out.println("decoderLog:"+DataFormatTransformUtil.bytesToHexString(bodyBytes));
					ProtoMessage message = ProtoMessage.parseFrom(bodyBytes); // 将body中protobuf字节码转成Student对象
					out.write(message); // 解析出一条消息
					return true;
				}
			}
		}

		@Override
		public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {

		}

		@Override
		public void dispose(IoSession session) throws Exception {

		}

	}
}
