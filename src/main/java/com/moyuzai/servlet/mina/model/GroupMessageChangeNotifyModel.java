package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import proto.MessageProtoBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupMessageChangeNotifyModel extends NotifyModel implements NotifyUser{

    private long groupId;

    public GroupMessageChangeNotifyModel(MessageProtoBuf.ProtoMessage message, IoSession session, Map<Long, Long> sessionMap,
                                         Set<Long> userIds, Map<String, Object> paramter) {
        super(message, session, sessionMap, userIds, paramter);
    }

    @Override
    protected boolean packingProtoMessage() {

        groupId = (long) paramterMap.get("groupId");
        if (DataFormatTransformUtil.isNullOrEmpty(groupId))
            return false;

        String groupName = (String) paramterMap.get("groupName");
        if (DataFormatTransformUtil.isNullOrEmpty(groupName))
            return false;

        long managerId = (long) paramterMap.get("managerId");
        if (DataFormatTransformUtil.isNullOrEmpty(managerId))
            return false;

        long picId = (long) paramterMap.get("picId");
        if (DataFormatTransformUtil.isNullOrEmpty(picId))
            return false;

        int amount = (int) paramterMap.get("amount");
        if (DataFormatTransformUtil.isNullOrEmpty(amount))
            return false;

        String managerName = (String) paramterMap.get("managerName");
        if (DataFormatTransformUtil.isNullOrEmpty(managerName))
            return false;

        String addUserIds = (String) paramterMap.get("addUsers");
        if (DataFormatTransformUtil.isNullOrEmpty(addUserIds))
            return false;

        Set<Long> addUserSet = DataFormatTransformUtil.StringToLongSet(addUserIds);
        String addUsersName;
        if (!DataFormatTransformUtil.isNullOrEmpty(addUserSet)){
            ServiceData addUsersNameData = userService.getUsersName(addUserSet);
            if (addUsersNameData.isState()){
                addUsersName = (String) addUsersNameData.getData();
                addUsersName = addUsersName.substring(0,addUsersName.length()-1);
            }else
                return false;
        }else
            return false;

        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("id",groupId);
        jsonMap.put("groupName",groupName);
        jsonMap.put("managerId",managerId);
        jsonMap.put("picId",picId);
        jsonMap.put("amount",amount);
        jsonMap.put("managerName",managerName);
        jsonMap.put("addUsersName",addUsersName);

        MessageProtoBuf.ProtoMessage message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.UPDATE_GROUP_NOTIFY,new Gson().toJson(jsonMap));
        return true;
    }

    @Override
    protected void handle() throws IoSessionIllegalException {
        boolean targetExist = checkoutTarget();
        if (targetExist){
            boolean isPackingSuccess = packingProtoMessage();
            if (!isPackingSuccess)
                return;
            notifyAllUsers(userIdSet,groupId,message);
        }
    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }
}
