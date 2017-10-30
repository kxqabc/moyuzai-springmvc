package com.moyuzai.servlet.mina.coder;

import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf.ProtoMessage;

public class MessageCodeFactory implements ProtocolCodecFactory {

	Logger logger = LoggerFactory.getLogger(this.getClass());

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
//			System.out.println("encoderLog:"+ DataFormatTransformUtil.bytesToHexString(bytes));
			int length = bytes.length;
			IoBuffer ioBuffer = IoBuffer.allocate(length+4);	//申请一个length+4长度的缓冲区，前4字节存放数据bytes
//			的长度，后面放bytes数组
			ioBuffer.setAutoExpand(true);
			ioBuffer.putInt(length);
			ioBuffer.put(bytes);
			ioBuffer.flip();	//写-->读
			out.write(ioBuffer);
		}

		@Override
		public void dispose(IoSession session) throws Exception {

		}

	}

	/**
	 * 这个方法是mina的解码器，十分重要，其中对于断包、粘包等处理需要认真思考，特别是对于剩余未读字节的判断以及对返回值的使用
	 * 返回值：
	 * 		 1）若此次解码后IoBuffer中仍有数据没有被处理完，则返回true，以调用解码方法再次进行解码
	 * 		 2）若此次解码后IoBuffer中的数据不够一次解析，则返回false，等待下次再有数据到来再解码
	 */
	class MyDecoder extends CumulativeProtocolDecoder {
		@Override
		protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			// 如果没有接收完Header部分（4字节），直接返回false
			if (in.remaining() < 4) {
				return false;
			} else {
				// 标记开始位置，如果一条消息没传输完成则返回到这个位置，以便reset
				in.mark();
				// 读取header部分，获取body长度(长度单位：字节数)
				int bodyLength = in.getInt();
				logger.info("decoderLog.bodyLength:"+bodyLength);
				// 如果body没有接收完整，直接返回false
				if (in.remaining() < bodyLength) {
					in.reset(); // IoBuffer position回到原来标记的地方
					return false;
				} else {
					byte[] bodyBytes = new byte[bodyLength];
					in.get(bodyBytes); // 读取body部分
					logger.info("decoderLog.body:"+DataFormatTransformUtil.bytesToHexString(bodyBytes));
					ProtoMessage message = ProtoMessage.parseFrom(bodyBytes); // 将body中protobuf字节码转成ProtoMessage对象
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
