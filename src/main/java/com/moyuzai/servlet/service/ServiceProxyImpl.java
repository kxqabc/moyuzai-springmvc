package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.*;
import com.moyuzai.servlet.mina.core.ServerHandler;
import com.moyuzai.servlet.mina.model.*;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ServiceProxyImpl implements ServiceProxy{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private ServerHandler serverHandler;

    @Override
    public UsersResponse getUserById(long id) throws DataClassErrorException {
        ServiceData serviceData = userService.getUserById(id);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof User){
                User user = (User) serviceData.getData();
                return new UsersResponse(MyEnum.GET_USER_SUCCESS, user.getId() + ","
                        + user.getUserName() + "," + user.getMobile());
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");

        }else
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
    }

    @Override
    public UsersResponse getUserByMobile(String mobile) throws DataClassErrorException {
        ServiceData serviceData = userService.getUserByMobile(mobile);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof User){
                User user = (User) serviceData.getData();
                return new UsersResponse(MyEnum.GET_USER_SUCCESS, user.getId() + ","
                        + user.getUserName() + "," + user.getMobile());
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");

        }else
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
    }

    @Override
    public UsersResponse userLogin(String mobile, String password) {
        ServiceData serviceData = userService.userLogin(mobile,password);
        if (serviceData.isState()){ //手机号、密码核对成功
            return new UsersResponse(MyEnum.LOGIN_SUCCESS,serviceData.getData());
        }else{
            if (serviceData.getStateNum() == 0) //没有找到用户
                return new UsersResponse(MyEnum.NEVER_REGISTER);
            else    //密码错误
                return new UsersResponse(MyEnum.PASSWORD_ERROR);
        }

    }

    @Override
    public UsersResponse getAllUsers(int offset, int limit) {
        ServiceData serviceData = userService.getAllUsers(offset,limit);
        if (serviceData.isState()){
            List<User> users = (List<User>) serviceData.getData();
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, users);
        }else
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
    }

    @Override
    public String getUsersName(Set<Long> userIdSet) {
        ServiceData serviceData = userService.getUsersName(userIdSet);
        return (String) serviceData.getData();
    }

    @Override
    public UsersResponse sendLoginMessage(String mobile, HttpSession httpSession) {
        ServiceData serviceData = userService.sendLoginMessage(mobile,httpSession);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.MESSAGE_SEND_SUCCESS);
        else if (serviceData.getStateNum() == 0)
            return new UsersResponse(MyEnum.USER_EXIST);
        else
            return new UsersResponse(MyEnum.MESSAGE_SEND_FAIL);
    }

    @Override
    public UsersResponse sendResetMessage(String mobile, HttpSession httpSession) {
        ServiceData serviceData = userService.sendResetMessage(mobile,httpSession);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.MESSAGE_SEND_SUCCESS);
        else if (serviceData.getStateNum() == 0)
            return new UsersResponse(MyEnum.NEVER_REGISTER);
        else
            return new UsersResponse(MyEnum.MESSAGE_SEND_FAIL);
    }

    @Override
    public UsersResponse justifyPassword(String mobile, String newPassword) throws DataAccessException{
        ServiceData serviceData = userService.justifyPassword(mobile,newPassword);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_SUCCESS);
        else if (serviceData.getStateNum() == 0)
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_ERROR);
    }

    @Override
    public UsersResponse userRegister(String userName, String mobile, String password) throws DataClassErrorException, DataAccessException {
        ServiceData serviceData = userService.userRegister(userName,mobile,password);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof User){
                User user = (User) serviceData.getData();
                return new UsersResponse(MyEnum.ADD_USER_SUCCESS,
                        user.getUserName() + "(" + user.getId() + ")");
            }else{
                logger.error("从serviceData中转换User类型数据出错！");
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
            }
        }else
            return new UsersResponse(MyEnum.ADD_USER_ERROR);
    }

    @Override
    public UsersResponse deleteUserById(long userId) {
        ServiceData serviceData = userService.deleteUserById(userId);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.DELETE_USER_SUCCESS);
        else
            return new UsersResponse(MyEnum.DELETE_USER_FAIL);
    }

    @Override
    public UsersResponse getAllGroups(int offset, int limit) {
        ServiceData serviceData = groupService.getAllGroups(offset,limit);
        if (serviceData.isState()){
            List<Group> groups = (List<Group>) serviceData.getData();
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, groups);
        }else
            return new UsersResponse(MyEnum.GROUP_IS_NOT_EXIST);
    }

    @Override
    public GroupResponse getGroupById(long groupId) throws DataClassErrorException {
        ServiceData serviceData = groupService.getGroupById(groupId);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof Group){
                Group group = (Group) serviceData.getData();
                int userAmount = userGroupService.getAmountInGroupById(groupId);
                group.setAmount(userAmount);
                return new GroupResponse(true,group);
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else
            return new GroupResponse(MyEnum.GROUP_IS_NOT_EXIST);        //这里的dto改变了
    }

    @Override
    public Group getGroupWithManName(long groupId) throws DataClassErrorException {
        ServiceData serviceData = groupService.getGroupWithManName(groupId);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof Group) {
                Group group = (Group) serviceData.getData();
                return group;
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else
            return null;
    }

    @Override
    public GroupResponse getGroupWithMoreDetail(long groupId) throws DataClassErrorException, TargetLostException {
        ServiceData serviceData = groupService.getGroupWithManName(groupId);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof Group) {
                Group group = (Group) serviceData.getData();
                int amount = userGroupService.getAmountInGroupById(groupId);
                group.setAmount(amount);
                return new GroupResponse(true,group);
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else
            throw new TargetLostException("目标群组丢失！");
    }

    @Override
    public UsersResponse deleteGroup(long managerId, long groupId) throws DeleteGroupException{
        ServiceData serviceData;
        serviceData = groupService.deleteGroup(managerId,groupId);

        if (serviceData.isState())
            return new UsersResponse(MyEnum.DISMISS_GROUP_SUCCESS);
        else if (serviceData.getStateNum() == 0)
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
        else
            return new UsersResponse(MyEnum.DISMISS_GROUP_FAIL);
    }

    @Override
    public UsersResponse deleteGroup(long groupId) {
        UsersResponse usersResponse  = groupService.deleteGroup(groupId);
        return usersResponse;
    }

    @Override
    public UsersResponse changeGroupPic(long groupId, long managerId, int picId,boolean cheched) throws AddPicIdErrorException{
        ServiceData serviceData = groupService.changeGroupPic(groupId,managerId,picId,cheched);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.CHANGE_GROUP_PIC_SUCCESS);
        else
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
    }

    @Override
    public UsersResponse changeGroupName(long groupId, long managerId, String groupName,boolean cheched) throws ChangeGroupNameException{
        ServiceData serviceData = groupService.changeGroupName(groupId,managerId,groupName,cheched);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.CHANGE_GROUP_NAME_SUCCESS);
        else
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
    }

    @Transactional
    @Override
    public UsersResponse updateGroupDate(long groupId, long managerId, Integer picId, String groupName, String addUsers, String minusUsers)
            throws AddPicIdErrorException,ChangeGroupNameException,AddUserToGroupErrorException,DeleteUserException,NumberFormatException{
        //核对群组是否存在
        boolean isGroupExist = groupService.checkGroupIsExist(groupId);
        if (!isGroupExist)
            return new UsersResponse(MyEnum.GROUP_IS_NOT_EXIST);
        //核对管理员是否存在
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        //是否该组的管理员
        boolean isManagerOfThisGroup = groupService.isManagerOfThisGroup(groupId,managerId);
        if (!isManagerOfThisGroup)
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
        //保存：添加、删除组员ID的集合
        Map<String,Object> usersMap = new HashMap<>();
        //检查picId参数是否为空，为空则表示没有修改
        if (!DataFormatTransformUtil.isNullOrEmpty(picId))
            groupService.changeGroupPic(groupId,managerId,picId,true);
        //检查groupName参数是否为空
        if (!DataFormatTransformUtil.isNullOrEmpty(groupName))
            groupService.changeGroupName(groupId,managerId,groupName,true);

        //踢出组员
        if (!DataFormatTransformUtil.isNullOrEmpty(minusUsers)){
            Set<Long> minusUsersIdSet = DataFormatTransformUtil.StringToLongSet(minusUsers);
            //如果minusUsers参数中包含管理者用户，则抛出异常
            if (minusUsersIdSet.contains(managerId))
                throw new DeleteUserException("修改群组信息时踢出管理者是非法的！");
            //将users依次踢出group
            if (!DataFormatTransformUtil.isNullOrEmpty(minusUsersIdSet)){
                userGroupService.deleteUsersOfGroup(minusUsersIdSet,groupId);
                //将踢出的用户id集合保存
                usersMap.put("minusUsers",minusUsersIdSet);
            }
        }

        //如果只改变了“踢出成员”这一项的话，就没必要做下去了；
        // 并且，不会再向map中put：unAffectedUsers、addUsers，刚好不用通知这两种人群
        if (DataFormatTransformUtil.isNullOrEmpty(picId) &&
                DataFormatTransformUtil.isNullOrEmpty(groupName) &&
                DataFormatTransformUtil.isNullOrEmpty(addUsers)){
            return new UsersResponse(MyEnum.CHANGE_GROUP_DATE_SUCCESS,usersMap);
        }

        //获取要通知“群组资料变化”的组员ID
        List<Long> unTouchedUserIds = null;
        ServiceData serviceData = userGroupService.queryAllUserIdOfGroup(groupId);
        if (serviceData.isState()){
            unTouchedUserIds = (List<Long>) serviceData.getData();
        }
        //将用户ID保存在dto的identity中
        if (!DataFormatTransformUtil.isNullOrEmpty(unTouchedUserIds)){
            Set<Long> unTouchUserSet = new HashSet<>();
            unTouchUserSet.addAll(unTouchedUserIds);
            if (unTouchUserSet.contains(managerId))
                unTouchUserSet.remove(managerId);
            if (!DataFormatTransformUtil.isNullOrEmpty(unTouchUserSet)){
                usersMap.put("unAffectedUsers",unTouchUserSet);
                logger.info("unAffectedUsers:"+unTouchUserSet.size());
            }
        }
        if (!DataFormatTransformUtil.isNullOrEmpty(addUsers)){
            logger.info("addUsers:"+addUsers);
            Set<Long> addUsersIdSet = DataFormatTransformUtil.StringToLongSet(addUsers);
            //不用再添加管理者用户
            if (addUsersIdSet.contains(managerId))
                addUsersIdSet.remove(managerId);
            //将users依次加入group中
            if (!DataFormatTransformUtil.isNullOrEmpty(addUsersIdSet)){
                userGroupService.addUsersToGroup(addUsersIdSet,groupId);
                usersMap.put("addUsers",addUsersIdSet);
            }
        }
        return new UsersResponse(MyEnum.CHANGE_GROUP_DATE_SUCCESS,usersMap);
    }

    /**
     * 创建群组（没有初始信息，如：头像、群组成员等）
     * @param groupName
     * @param managerId
     * @return
     * @throws CreateGroupErrorException
     * @throws AddUserToGroupErrorException
     * @throws DataClassErrorException
     */
    @Transactional
    @Override
    public UsersResponse createGroup(String groupName, long managerId) throws CreateGroupErrorException,
    AddUserToGroupErrorException,DataClassErrorException{
        //检查要求创建群组的用户是否存在
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        ServiceData serviceData;
        //创建群组
        serviceData = groupService.createGroup(groupName,managerId);
        if (serviceData.isState()){ //如果创建群组成功
            if (serviceData.getData() instanceof Group){
                Group group = (Group) serviceData.getData();
                //将管理员添加到关系表中
                ServiceData addUserResult = userGroupService.addUserToGroup(managerId,group.getId());
                //如果添加成功
                if (addUserResult.isState())
                    return new UsersResponse(MyEnum.CREATE_GROUP_SUCCESS,
                            group.getGroupName()+"("+group.getId()+")");
                else
                    throw new AddUserToGroupErrorException("创建群组时添加管理员信息出错！");
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else if (serviceData.getStateNum() == 0) //创建失败，原因：群组已经存在
            return new UsersResponse(MyEnum.GROUP_EXIST);
        else
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
    }

    @Transactional
    @Override
    public UsersResponse createGroupWithInit(Set<Long> userIdSet, long managerId, String groupName, int picId) throws
            CreateGroupErrorException, AddPicIdErrorException, AddUserToGroupErrorException, DataClassErrorException {
        //用户集合中有部分用户未注册
        boolean isAllUserExist = userService.isAllUserExist(userIdSet);
        if (!isAllUserExist)
            return new UsersResponse(MyEnum.USERS_NOT_REGISTER);
        //管理者未注册
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        //创建群组
        ServiceData createGroupResult = groupService.createGroup(groupName,managerId);
        if (createGroupResult.isState()){
            if (createGroupResult.getData() instanceof Group){
                Group group = (Group) createGroupResult.getData();
                long groupId = group.getId();
                //更改群组头像
                groupService.changeGroupPic(groupId,managerId,picId,true);
                //将用户添加到群组中
                userGroupService.addUsersToGroup(userIdSet,groupId);
                return new UsersResponse(MyEnum.CREATE_GROUP_SUCCESS,groupName+"("+groupId+")");
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else {
            //这里需要抛出异常而不是返回usersresponse，因为使事务管理生效回滚
            throw new CreateGroupErrorException("创建群组（含初始化参数）失败！");
        }
    }

    @Override
    public UsersResponse getAll(int offset, int resultCount) {
        return userGroupService.getAll(offset,resultCount);
    }


    @Override
    public UsersResponse getUsersOfGroup(long groupId) {
        ServiceData serviceData = userGroupService.getUsersOfGroup(groupId);
        if (serviceData.isState()){
            List<User> users = (List<User>) serviceData.getData();
            return new UsersResponse(MyEnum.GET_USER_SUCCESS,users);
        }else
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
    }

    @Transactional
    @Override
    public UsersResponse joinGroup(long userId, long groupId) throws AddUserToGroupErrorException{
        /**判断用户、群组是否存在*/
        boolean isUserExist = userService.isUserExist(userId);
        boolean isGroupExist = groupService.checkGroupIsExist(groupId);
        if (!isUserExist || !isGroupExist)
            return new UsersResponse(MyEnum.GROUP_USER_INFO_ERROR);
        /**判断用户是否已经加入群组，防止重复加入同一个群组*/
        boolean isJoined = userGroupService.isJoined(groupId,userId);
        if (isJoined)
            return new UsersResponse(MyEnum.IS_JOINED);
        /**执行加入动作*/
        ServiceData addUserResult;
        addUserResult = userGroupService.addUserToGroup(userId,groupId);
        if (addUserResult.isState())
            return new UsersResponse(MyEnum.JOIN_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.JOIN_GROUP_FAIL);
    }

    @Override
    public UsersResponse signoutFromGroup(long userId, long groupId) throws DeleteUserException{
        //管理者不能主动退出群组，只能解散一个组，否则该组将缺少管理员
        if (groupService.isManagerOfThisGroup(groupId,userId))
            return new UsersResponse(MyEnum.MANAGER_CANT_SIGNOUT);
        ServiceData serviceData = userGroupService.signoutFromGroup(userId,groupId);
        if (serviceData.isState()){
            MyEnum.SIGNOUT_SUCCESS.setStateInfo("退出群"+groupId+"成功！");
            return new UsersResponse(MyEnum.SIGNOUT_SUCCESS);
        }else if (serviceData.getStateNum()==0){
            return new UsersResponse(MyEnum.NOT_IN_GROUP_ERROR);
        }else {
            MyEnum.SIGNOUT_GROUP_FAIL.setStateInfo("退出群"+groupId+"失败！");
            return new UsersResponse(MyEnum.SIGNOUT_GROUP_FAIL);
        }
    }

    @Override
    public UsersResponse queryAllUserIdOfGroup(long groupId) {
        ServiceData serviceData = userGroupService.queryAllUserIdOfGroup(groupId);
        if (serviceData.isState()){
            return new UsersResponse(MyEnum.GET_USER_SUCCESS,serviceData.getData());
        }else
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
    }

    @Override
    public UsersResponse deleteUserOfGroup(long id) {
        UsersResponse usersResponse = userGroupService.deleteUsersOfGroup(id);
        return usersResponse;
    }

    @Override
    public void notifyUserIsPulledIntoGroup(Set<Long> userIdSet, long groupId) throws IoSessionIllegalException, DataClassErrorException, TargetLostException {
        Map<String,Object> paramterMap = new HashMap<>();
        GroupResponse groupResponse= getGroupWithMoreDetail(groupId);
        if (groupResponse.isState()){
            Group group = groupResponse.getGroup();
            paramterMap.put("group",group);
            ServerHandler.Notify pulledIntoGroupNotify = serverHandler.new Notify(
                    new PulledIntoGroupNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap));
            pulledIntoGroupNotify.notifyUser();
            logger.info("通知用户已经被拉入群组成功");
        }else
            logger.info("通知用户已经被拉入群组失败");

    }

    @Override
    public void notifySomeJoined(long userId, long groupId) throws IoSessionIllegalException {
        ServiceData serviceData = userGroupService.queryAllUserIdOfGroup(groupId);
        if (serviceData.isState()){
            List<Long> userIds = (List<Long>) serviceData.getData();
            Set<Long> userIdSet = new HashSet<>();
            userIdSet.addAll(userIds);
            //不用通知申请加入的用户
            if (userIdSet.contains(userId))
                userIdSet.remove(userId);
            Map<String,Object> paramterMap = new HashMap<>();
            paramterMap.put("userId",userId);
            paramterMap.put("groupId",groupId);
            ServerHandler.Notify someoneJoinGroupNotify = serverHandler.new Notify(
                    new JoinGroupNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap,userService));
            someoneJoinGroupNotify.notifyUser();
        }
    }

    @Override
    public void notifyUserGroupIsDisMissed(Set<Long> userIdSet, long groupId) throws IoSessionIllegalException {
        Map<String,Object> paramterMap = new HashMap<>();
        paramterMap.put("groupId",groupId);
        ServerHandler.Notify dismissGroupNotify = serverHandler.new Notify(
                new GroupDissmissNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap));
        dismissGroupNotify.notifyUser();
    }

    @Override
    public void notifyUsersGroupMessageChange(Set<Long> userIdSet, Group group, String addUsers) throws IoSessionIllegalException {
        if (DataFormatTransformUtil.isNullOrEmpty(group) || DataFormatTransformUtil.isNullOrEmpty(userIdSet)) {
            logger.warn("通知群组信息改变时形参group或userIdSet为null！");
            return;
        }
        Map<String,Object> paramterMap = new HashMap<>();
        paramterMap.put("groupId",group.getId());
        paramterMap.put("groupName",group.getGroupName());
        paramterMap.put("managerId",group.getManagerId());
        paramterMap.put("picId",group.getPicId());
        paramterMap.put("amount",group.getAmount());
        paramterMap.put("managerName",group.getManagerName());
        paramterMap.put("addUsers",addUsers);
        ServerHandler.Notify groupChangeNotify = serverHandler.new Notify(
                new GroupMessageChangeNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap,userService));
        groupChangeNotify.notifyUser();
    }

    @Override
    public void notifyUsersIsKickout(Set<Long> userIdSet, long groupId) throws IoSessionIllegalException {
        Map<String,Object> paramterMap = new HashMap<>();
        paramterMap.put("groupId",groupId);
        ServerHandler.Notify kickoutFromGroupNotify = serverHandler.new Notify(
                new KickoutGroupNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap));
        kickoutFromGroupNotify.notifyUser();
    }

    @Override
    public void notifySomeoneQuit(long userId, long groupId) throws DataClassErrorException, TargetLostException, IoSessionIllegalException {
        Map<String,Object> paramterMap = new HashMap<>();
        ServiceData userIdsData = userGroupService.queryAllUserIdOfGroup(groupId);
        GroupResponse groupResponse = getGroupWithMoreDetail(groupId);
        Group group;
        long managerId;
        if (groupResponse.isState()) {
            group = groupResponse.getGroup();
            managerId = group.getManagerId();
        }
        else {
            return;
        }
        paramterMap.put("groupId",groupId);
        paramterMap.put("userId",userId);
        paramterMap.put("amount",group.getAmount());
        paramterMap.put("managerId",managerId);
        if (userIdsData.isState() && groupResponse.isState()) {
            List<Long> userIds = (List<Long>) userIdsData.getData();
            Set<Long> userIdSet = new HashSet<>();
            userIdSet.addAll(userIds);
            if (userIdSet.contains(managerId))
                userIdSet.remove(managerId);
            ServerHandler.Notify someoneQuitNotify = serverHandler.new Notify(
                    new SomeoneQuitNotifyModel(null,serverHandler.getIoSessionMap(),serverHandler.getIdMap(),userGroupService,userIdSet,paramterMap));
            someoneQuitNotify.notifyUser();
        }
    }


}
