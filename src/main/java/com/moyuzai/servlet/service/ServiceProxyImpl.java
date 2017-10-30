package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.*;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proto.MessageProtoBuf;

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
    private MinaService minaService;

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
    public UsersResponse userLogin(String mobile, String password) throws DataClassErrorException {
        ServiceData serviceData = userService.userLogin(mobile,password);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof User){
                User user = (User) serviceData.getData();
                if (password.equals(user.getPassword()))
                    return new UsersResponse(MyEnum.LOGIN_SUCCESS,
                            user.getUserName()+"("+user.getId()+")");
                else
                    return new UsersResponse(MyEnum.PASSWORD_ERROR);
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
        }else
            return new UsersResponse(MyEnum.NEVER_REGISTER);
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
    public UsersResponse justifyPassword(String mobile, String newPassword) {
        ServiceData serviceData = userService.justifyPassword(mobile,newPassword);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_SUCCESS);
        else if (serviceData.getStateNum() == 0)
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_ERROR);
    }

    @Override
    public UsersResponse userRegister(String userName, String mobile, String password) throws DataClassErrorException {
        ServiceData serviceData = userService.userRegister(userName,mobile,password);
        if (serviceData.isState()){
            if (serviceData.getData() instanceof User){
                User user = (User) serviceData.getData();
                return new UsersResponse(MyEnum.ADD_USER_SUCCESS,
                        user.getUserName() + "(" + user.getId() + ")");
            }else
                throw new DataClassErrorException("查询数据库数据不符合期望类型！");
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
    public UsersResponse changeGroupPic(long groupId, long managerId, int picId,boolean cheched) {
        ServiceData serviceData = groupService.changeGroupPic(groupId,managerId,picId,cheched);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.CHANGE_GROUP_PIC_SUCCESS);
        else
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
    }

    @Override
    public UsersResponse changeGroupName(long groupId, long managerId, String groupName,boolean cheched) {
        ServiceData serviceData = groupService.changeGroupName(groupId,managerId,groupName,cheched);
        if (serviceData.isState())
            return new UsersResponse(MyEnum.CHANGE_GROUP_NAME_SUCCESS);
        else
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
    }

    @Override
    public UsersResponse updateGroupDate(long groupId, long managerId, Integer picId, String groupName,
                                         String addUsers, String minusUsers) {
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
        try{
            //检查picId参数是否为空，为空则表示没有修改
            if (!DataFormatTransformUtil.isNullOrEmpty(picId))
                groupService.changeGroupPic(groupId,managerId,picId,true);
            //检查groupName参数是否为空
            if (!DataFormatTransformUtil.isNullOrEmpty(groupName))
                groupService.changeGroupName(groupId,managerId,groupName,true);

            //踢出组员
            if (!DataFormatTransformUtil.isNullOrEmpty(minusUsers)){
                Set<Long> minusUsersIdSet = DataFormatTransformUtil.StringToLongSet(minusUsers);
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
            List<Long> unTouchedUserIds = userGroupService.queryAllUserIdOfGroup(groupId);
            //将用户ID保存在dto的identity中
            if (!DataFormatTransformUtil.isNullOrEmpty(unTouchedUserIds)){
                Set<Long> unTouchUserSet = new HashSet<>();
                unTouchUserSet.addAll(unTouchedUserIds);
                unTouchUserSet.remove(managerId);
                if (!DataFormatTransformUtil.isNullOrEmpty(unTouchUserSet)){
                    usersMap.put("unAffectedUsers",unTouchUserSet);
                    logger.info("unAffectedUsers:"+unTouchUserSet.size());
                }
            }
            if (!DataFormatTransformUtil.isNullOrEmpty(addUsers)){
                logger.info("addUsers:"+addUsers);
                Set<Long> addUsersIdSet = DataFormatTransformUtil.StringToLongSet(addUsers);
                logger.info("addUsersIdSet.size:"+addUsersIdSet.size());
                //将users依次加入group中
                if (!DataFormatTransformUtil.isNullOrEmpty(addUsersIdSet)){
                    userGroupService.addUsersToGroup(addUsersIdSet,groupId);
                    usersMap.put("addUsers",addUsersIdSet);
                }
            }
        }catch (AddPicIdErrorException e1){
            throw e1;
        }catch (ChangeGroupNameException e2){
            throw e2;
        }catch (AddUserToGroupErrorException e3){
            throw e3;
        }catch (DeleteUserException e4){
            throw e4;
        }catch (NumberFormatException e5){
            throw e5;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new MoyuzaiInnerErrorException("墨鱼仔出现内部错误！");
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
        ServiceData serviceData = null;
        try{
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
            }else {  //创建失败，原因：群组已经存在
                return new UsersResponse(MyEnum.GROUP_EXIST);
            }
        }catch (CreateGroupErrorException e1){
            logger.error(e1.getMessage());
            throw e1;
        }catch (AddUserToGroupErrorException e2){
            logger.error(e2.getMessage());
            throw e2;
        }catch (DataClassErrorException e3){
            e3.printStackTrace();
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new MoyuzaiInnerErrorException("创建群组时，墨鱼仔发生内部错误！");
        }
    }

    @Transactional
    @Override
    public UsersResponse createGroupWithInit(Set<Long> userIdSet, long managerId, String groupName, int picId) throws
    CreateGroupErrorException,AddPicIdErrorException,AddUserToGroupErrorException{
        //用户集合中有部分用户未注册
        boolean isAllUserExist = userService.isAllUserExist(userIdSet);
        if (!isAllUserExist)
            return new UsersResponse(MyEnum.USERS_NOT_REGISTER);
        //管理者未注册
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        try {
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
                throw new CreateGroupErrorException("创建群组（带初始化参数）出错！");
            }
        }catch (CreateGroupErrorException e1){
            throw e1;
        }catch (AddUserToGroupErrorException e2){
            throw e2;
        }catch (AddPicIdErrorException e3){
            throw e3;
        }catch (DataClassErrorException e4) {
            e4.printStackTrace();
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new MoyuzaiInnerErrorException("创建群组时，墨鱼仔发生内部错误！");
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
        ServiceData addUserResult = null;
        try {
            addUserResult = userGroupService.addUserToGroup(userId,groupId);
        }catch (AddUserToGroupErrorException e1){
            throw e1;
        }
        if (addUserResult.isState())
            return new UsersResponse(MyEnum.JOIN_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.JOIN_GROUP_FAIL);
    }

    @Override
    public UsersResponse signoutFromGroup(long userId, long groupId) {
        try{
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
        }catch (DeleteUserException e1){
            throw e1;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public List<Long> queryAllUserIdOfGroup(long groupId) {
        return null;
    }

    @Override
    public UsersResponse addUserPicToGroup(Set<Long> userIdSet, long managerId, String groupName, int picId) {
        return null;
    }

    @Override
    public ServiceData addUserToGroup(long userId, long groupId) {
        return null;
    }

    @Override
    public void addUsersToGroup(Set<Long> userIdSet, long groupId) {

    }

    @Override
    public void deleteUsersOfGroup(Set<Long> userIdSet, long groupId) {

    }

    @Override
    public UsersResponse deleteUsersOfGroup(long id) {
        return null;
    }

    @Override
    public int insertOfflineText(MessageProtoBuf.ProtoMessage protoMessage, long userId, long groupId) {
        return 0;
    }

    @Override
    public List<MessageProtoBuf.ProtoMessage> getOfflineText(long userId) {
        return null;
    }

}
