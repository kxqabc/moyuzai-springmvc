package com.moyuzai.servlet.util;

import proto.MessageProtoBuf;

import java.util.HashSet;
import java.util.Set;

public class DataFormatTransformUtil {

    public DataFormatTransformUtil() {
    }

    /**
     * 将信息转换为protoMessage
     * @param type
     * @param from
     * @param to
     * @param body
     * @return
     */
    public static MessageProtoBuf.ProtoMessage packingToProtoMessage(MessageProtoBuf.ProtoMessage.Type type,
                                                                     String from,String to,String body){
        MessageProtoBuf.ProtoMessage.Builder builder =
                MessageProtoBuf.ProtoMessage.newBuilder();
        builder.setType(type);
        builder.setFrom(from);
        builder.setTo(to);
        builder.setTime(""+System.currentTimeMillis());
        builder.setBody(body);
        return builder.build();
    }

    public static Set<Long> StringToLongSet(String message){
        if (message == null || "".equals(message))
            return null;
        Set<Long> longSet = new HashSet<>();      //保存了所有ID的集合
        String[] longArray = message.split(",");    //切割字符串
        /**从String中提取用户ID信息*/
        for (String element:longArray){
            if (element!=null&&(!"".equals(element)))
                longSet.add(Long.parseLong(element));
        }
        return longSet;
    }
}
