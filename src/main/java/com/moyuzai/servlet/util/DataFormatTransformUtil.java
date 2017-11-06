package com.moyuzai.servlet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.MessageProtoBuf;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataFormatTransformUtil{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataFormatTransformUtil() {
    }

    /**
     * 将信息转换为protoMessage
     * @param type
     * @param body
     * @return
     */
    public static MessageProtoBuf.ProtoMessage packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type type, String body){
        MessageProtoBuf.ProtoMessage.Builder builder =
                MessageProtoBuf.ProtoMessage.newBuilder();
        builder.setType(type);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.setTime(dateFormat.format(new Date()));
        builder.setBody(body);
        return builder.build();
    }

    /**
     * 加上信息发送的时间，也作为这条信息的标识符
     * @param type
     * @param sendTime
     * @param body
     * @return
     */
    public static MessageProtoBuf.ProtoMessage packingToProtoMessageOption(MessageProtoBuf.ProtoMessage.Type type,
                                                                           String sendTime, String body){
        MessageProtoBuf.ProtoMessage.Builder builder =
                MessageProtoBuf.ProtoMessage.newBuilder();
        builder.setType(type);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.setTime(sendTime + "," + dateFormat.format(new Date()));
        builder.setBody(body);
        return builder.build();
    }

    public static Set<Long> StringToLongSet(String message)
    throws NumberFormatException{
        if (message == null || "".equals(message))
            return null;
        Set<Long> longSet = new HashSet<>();      //保存了所有ID的集合
        //判断字符串中是否包含逗号
        if (message.contains(",")){
            String[] longArray = message.split(",");    //切割字符串
            if (DataFormatTransformUtil.isNullOrEmpty(longArray))
                return null;
            /**从String中提取用户ID信息*/
            long num;
            for (String element:longArray){
                if (!DataFormatTransformUtil.isNullOrEmpty(element)){
                    try {
                        num = Long.parseLong(element);
                    }catch (NumberFormatException e1){
                        System.out.println(e1.getMessage());
                        throw e1;
                    }
                    longSet.add(num);
                }
            }
        }else {
            //如果parseLong出现错误不能转为long，则返回null
            try {
                long userId = Long.parseLong(message);
                longSet.add(userId);
            }catch (NumberFormatException e){
                System.out.println(e.getMessage());
                throw e;
            }
        }
        return longSet;
    }

    public static <T> boolean isNullOrEmpty(T data){
        if (data == null){
            return true;
        }
        //字符串
        if (data instanceof CharSequence){
            return ((CharSequence)(CharSequence) data).length()==0;
        }
        //集合
        if (data instanceof Collection){
            return ((Collection)data).isEmpty();
        }
        //Map
        if (data instanceof Map){
            return ((Map) data).isEmpty();
        }
        return false;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
