package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dao.GroupDao;
import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.entity.UserGroup;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.*;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class GroupServiceImpl implements GroupService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private UserService userService;

    @Override
    public UsersResponse getAllGroups(int offset, int limit) {
        List<Group> groups = groupDao.queryAll(offset,limit);
        if (groups == null || "".equals(groups) || groups.size() == 0)
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, groups);
    }

    @Override
    public GroupResponse getGroupById(long groupId) {
        Group group = groupDao.queryGroupWithManNameById(groupId);
        if (group == null || "".equals(group))
            return new GroupResponse(MyEnum.GROUP_IS_NOT_EXIST);        //这里的dto改变了
        else{
            int userAmount = userGroupService.getAmountInGroupById(groupId);
            logger.info("userAmount:"+userAmount);
            group.setAmount(userAmount);
            return new GroupResponse(true,group);
        }
    }

    @Override
    public Group queryGroupByGroupNameManId(String groupName, long managerId) {
        Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
        return group;
    }

    @Override
    public UsersResponse changeGroupPic(long groupId,long managerId, int picId) {
        boolean isGroupExist = checkGroupIsExist(groupId);
        //核对群组是否存在
        if (!isGroupExist)
            return new UsersResponse(MyEnum.GROUP_IS_NOT_EXIST);
        boolean isManagerExist = userService.isUserExist(managerId);
        //核对管理员是否存在
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        boolean isManagerOfThisGroup = isManagerOfThisGroup(groupId,managerId);
        //不是该组的管理员
        if (!isManagerOfThisGroup)
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
        int effextCount = groupDao.updateGroupPic(groupId,picId);
        return new UsersResponse(MyEnum.CHANGE_GROUP_PIC_SUCCESS);
    }

    @Override
    public UsersResponse changeGroupName(long groupId, long managerId, String groupName) {
        boolean isGroupExist = checkGroupIsExist(groupId);
        //核对群组是否存在
        if (!isGroupExist)
            return new UsersResponse(MyEnum.GROUP_IS_NOT_EXIST);
        boolean isManagerExist = userService.isUserExist(managerId);
        //核对管理员是否存在
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        boolean isManagerOfThisGroup = isManagerOfThisGroup(groupId,managerId);
        //不是该组的管理员
        if (!isManagerOfThisGroup)
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
        int effextCount = groupDao.updateGroupName(groupId,groupName);
        return new UsersResponse(MyEnum.CHANGE_GROUP_NAME_SUCCESS);
    }

    /**
     * 更改群组资料，包括群组头像、群组名称、增减用户
     * @param groupId
     * @param managerId
     * @param picId
     * @param groupName
     * @return
     */
    @Transactional
    @Override
    public UsersResponse updateGroupDate(long groupId, long managerId, Integer picId, String groupName,
                                         String addUsers,String minusUsers) {
        boolean isGroupExist = checkGroupIsExist(groupId);
        //核对群组是否存在
        if (!isGroupExist)
            return new UsersResponse(MyEnum.GROUP_IS_NOT_EXIST);
        boolean isManagerExist = userService.isUserExist(managerId);
        //核对管理员是否存在
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        boolean isManagerOfThisGroup = isManagerOfThisGroup(groupId,managerId);
        //不是该组的管理员
        if (!isManagerOfThisGroup)
            return new UsersResponse(MyEnum.NOT_THE_MANAGER_OF_THIS_GROUP);
        //检验传入参数，确定更改项
        try {
            //检查picId参数是否为空，为空则表示没有修改
            if (picId!=null && (!"".equals(picId)))
                groupDao.updateGroupPic(groupId,picId);
            //检查groupName参数是否为空
            if (groupName!=null && (!"".equals(groupName)))
                groupDao.updateGroupName(groupId,groupName);
            if (addUsers!=null && (!"".equals(addUsers))){
                Set<Long> addUsersIdSet = DataFormatTransformUtil.StringToLongSet(addUsers);
                //将users依次加入group中
                if (addUsersIdSet!=null && (!"".equals(addUsersIdSet) && addUsersIdSet.size()>0 )){
                    userGroupService.addUsersToGroup(addUsersIdSet,groupId);
                }
            }
            if (minusUsers!=null && (!"".equals(minusUsers))){
                Set<Long> minusUsersIdSet = DataFormatTransformUtil.StringToLongSet(minusUsers);
                //将users依次踢出group
                if (minusUsersIdSet!=null && (!"".equals(minusUsersIdSet) && minusUsersIdSet.size()>0 )){
                    userGroupService.deleteUsersOfGroup(minusUsersIdSet,groupId);
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
        }catch (Exception e){
            logger.error(e.toString(),e);
            throw new MoyuzaiInnerErrorException("墨鱼仔出现内部错误！");
        }
        return new UsersResponse(MyEnum.CHANGE_GROUP_DATE_SUCCESS);
    }

    @Override
    public UsersResponse createGroup(String groupName, long managerId) {
        boolean isExist = checkGroupIsExist(groupName,managerId);
        if (isExist)
            return new UsersResponse(MyEnum.GROUP_EXIST);
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        int effectCount = groupDao.createGroup(groupName,managerId);    //创建群组影响的行数
        if (effectCount>0){//影响行数大于0，则说明插入成功
            /**查找群组，为了讲群组id等信息返回给用户*/
            Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
            return new UsersResponse(MyEnum.CREATE_GROUP_SUCCESS,
                    group.getGroupName()+"("+group.getId()+")");
        }
        else
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
    }

    /**
     * 这里应该考虑service之间怎么调用，降低耦合!(用到了“事务管理”)
     * @param userIdSet
     * @param managerId
     * @param groupName
     * @param picId
     * @return
     */
    @Override
    @Transactional
    public UsersResponse createGroupWithInit(Set<Long> userIdSet,long managerId,
                                      String groupName,int picId)
            throws CreateGroupErrorException,AddPicIdErrorException,AddUserToGroupErrorException{
        boolean isAllUserExist = userService.isAllUserExist(userIdSet);
        if (!isAllUserExist)        //用户集合中有部分用户未注册
            return new UsersResponse(MyEnum.USERS_NOT_REGISTER);
        boolean isManagerExist = userService.isUserExist(managerId);
        if (!isManagerExist)        //管理者未注册
            return new UsersResponse(MyEnum.MANAGER_ERROR);
        try {
            UsersResponse usersResponse = createGroup(groupName,managerId);     //调用上面没有初始化的群组创建方法
            if (usersResponse.isState()){      //群组创建成功
                logger.info("创建群组（不带初始化信息）成功！");
                return userGroupService.addUserPicToGroup(userIdSet,managerId,groupName,picId);
            }else
                return usersResponse;
        }catch (CreateGroupErrorException e1){
            throw e1;
        }catch (AddPicIdErrorException e2){
            throw e2;
        }catch (AddUserToGroupErrorException e3){
            throw e3;
        }catch (Exception e){
            logger.error(e.toString(),e);
            throw new MoyuzaiInnerErrorException("墨鱼仔出现内部错误！");
        }
    }

    /**
     * 判断群组是否已经存在
     * @param groupName
     * @param managerId
     * @return
     */
    @Override
    public boolean checkGroupIsExist(String groupName, long managerId){
        Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
        if (group == null || "".equals(group))
            return false;
        else
            return true;
    }

    @Override
    public boolean checkGroupIsExist(long groupId) {
        Group group = groupDao.queryById(groupId);
        if (group == null || "".equals(group))
            return false;
        else
            return true;
    }

    @Override
    public boolean isManagerOfThisGroup(long groupId, long managerId) {
        Group group = groupDao.queryById(groupId);
        if (group == null|| "".equals(group))
            return false;
        if (managerId == group.getManagerId())
            return true;
        return false;
    }

    @Override
    public UsersResponse deleteGroup(long managerId, long groupId) {
        int effectCount = groupDao.deleteGroup(managerId,groupId);
        if (effectCount>0)
            return new UsersResponse(MyEnum.DISMISS_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.DISMISS_GROUP_FAIL);
    }

    @Override
    public UsersResponse deleteGroup(long groupId) {
        int effectCount = groupDao.deleteGroupById(groupId);
        if (effectCount>0)
            return new UsersResponse(MyEnum.DISMISS_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.DISMISS_GROUP_FAIL);
    }

}
