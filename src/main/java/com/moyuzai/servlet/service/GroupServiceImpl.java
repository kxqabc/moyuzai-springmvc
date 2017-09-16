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
import com.moyuzai.servlet.exception.AddPicIdErrorException;
import com.moyuzai.servlet.exception.AddUserToGroupErrorException;
import com.moyuzai.servlet.exception.CreateGroupErrorException;
import com.moyuzai.servlet.exception.MoyuzaiInnerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
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
    public GroupResponse getGroupById(long groupId) {
        Group group = groupDao.queryById(groupId);
        if (group == null || "".equals(group))
            return new GroupResponse(MyEnum.GROUP_IS_NOT_EXIST);        //这里的dto改变了
        else{
            User user = userDao.queryById(group.getManagerId());  //获取群组管理员信息
            return new GroupResponse(true,group,user.getUserName());
        }
    }

    @Override
    public Group queryGroupByGroupNameManId(String groupName, long managerId) {
        Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
        return group;
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
                return userGroupService.addUserToGroup(userIdSet,managerId,groupName,picId);
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
    public UsersResponse deleteGroup(long managerId, long groupId) {
        int effectCount = groupDao.deleteGroup(managerId,groupId);
        if (effectCount>0)
            return new UsersResponse(MyEnum.DISMISS_GROUP_SUCCESS);
        else
            return new UsersResponse(MyEnum.DISMISS_GROUP_FAIL);
    }

}
