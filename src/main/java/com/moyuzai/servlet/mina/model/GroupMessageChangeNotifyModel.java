package com.moyuzai.servlet.mina.model;

import com.google.gson.Gson;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.exception.IoSessionIllegalException;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.service.UserService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import proto.MessageProtoBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupMessageChangeNotifyModel extends NotifyModel implements NotifyUser{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private UserService userService;

    private long groupId;

    public GroupMessageChangeNotifyModel(MessageProtoBuf.ProtoMessage message, Map<Long, IoSession> sessionMap, Map<Long, Long> idMap,
                                         UserGroupService userGroupService, Set<Long> userIdSet, Map<String, Object> paramterMap, UserService userService) {
        super(message, sessionMap, idMap, userGroupService, userIdSet, paramterMap);
        this.userService = userService;
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

        int picId = (int) paramterMap.get("picId");
        if (DataFormatTransformUtil.isNullOrEmpty(picId))
            return false;

        int amount = (int) paramterMap.get("amount");
        if (DataFormatTransformUtil.isNullOrEmpty(amount))
            return false;

        String managerName = (String) paramterMap.get("managerName");
        if (DataFormatTransformUtil.isNullOrEmpty(managerName))
            return false;

        String addUserIds = (String) paramterMap.get("addUsers");
        String addUsersName = "";   //设置为空字符串在json中仍然保留addUsers字段，若设为null则不保留
        if (!DataFormatTransformUtil.isNullOrEmpty(addUserIds)){
            Set<Long> addUserSet = DataFormatTransformUtil.StringToLongSet(addUserIds);
            if (!DataFormatTransformUtil.isNullOrEmpty(addUserSet)){
                if (addUserSet.contains(managerId))
                    addUserSet.remove(managerId);
                if (!DataFormatTransformUtil.isNullOrEmpty(addUserSet)){
                    ServiceData addUsersNameData = userService.getUsersName(addUserSet);
                    if (addUsersNameData.isState()){
                        addUsersName = (String) addUsersNameData.getData();
                        addUsersName = addUsersName.substring(0,addUsersName.length()-1);
                    }
                }

            }
        }

        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("id",groupId);
        jsonMap.put("groupName",groupName);
        jsonMap.put("managerId",managerId);
        jsonMap.put("picId",picId);
        jsonMap.put("amount",amount);
        jsonMap.put("managerName",managerName);
        jsonMap.put("addUsers",addUsersName);

        message = DataFormatTransformUtil.packingToProtoMessageOption(
                MessageProtoBuf.ProtoMessage.Type.UPDATE_GROUP_NOTIFY,new Gson().toJson(jsonMap));
        return true;
    }

    @Override
    protected void handle() throws IoSessionIllegalException {
        boolean targetExist = checkoutTarget();
        if (targetExist){
            boolean isPackingSuccess = packingProtoMessage();
            logger.info("组装通知信息成功？"+isPackingSuccess);
            if (!isPackingSuccess)
                return;
            notifyAllUsers(userIdSet,groupId,message,userGroupService);
        }
    }

    @Override
    public void notifyUsers() throws IoSessionIllegalException {
        handle();
    }
}
