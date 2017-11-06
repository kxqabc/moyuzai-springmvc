package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dao.GroupDao;
import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dao.UserGroupDao;
import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class GroupServiceImpl implements GroupService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupDao groupDao;


    @Override
    public ServiceData getAllGroups(int offset, int limit) {
        List<Group> groups = groupDao.queryAll(offset,limit);
        if (DataFormatTransformUtil.isNullOrEmpty(groups))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,groups);
    }

    @Override
    public ServiceData getGroupById(long groupId) {
        Group group = groupDao.queryGroupWithManNameById(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,group);
    }

    @Override
    public Group queryGroupByGroupNameManId(String groupName, long managerId) {
        Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
        return group;
    }

    @Override
    public ServiceData getGroupWithManName(long groupId) {
        Group group = groupDao.queryGroupWithManNameById(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,group);
    }

    @Override
    public ServiceData changeGroupPic(long groupId,long managerId, int picId,boolean checked) {
        if (!checked){//是否该组的管理员
            boolean isManagerOfThisGroup = isManagerOfThisGroup(groupId,managerId);
            if (!isManagerOfThisGroup)
                return new ServiceData(false,null);
        }
        try{
            groupDao.updateGroupPic(groupId,picId);
        }catch (Exception e){
            e.printStackTrace();
            return new ServiceData(false,null);
        }
        return new ServiceData(true,null);
    }

    @Override
    public ServiceData changeGroupName(long groupId, long managerId, String groupName,boolean checked) throws ChangeGroupNameException{
        if (!checked){
            //是否该组的管理员
            boolean isManagerOfThisGroup = isManagerOfThisGroup(groupId,managerId);
            if (!isManagerOfThisGroup)
                return new ServiceData(false,null);
        }
        try{
            groupDao.updateGroupName(groupId,groupName);
        }catch (DataAccessException de){
            de.printStackTrace();
            throw new ChangeGroupNameException("修改群组名称时数据库发生异常！");
        }
        return new ServiceData(true,null);
    }


    @Override
    public ServiceData createGroup(String groupName, long managerId)
            throws CreateGroupErrorException{
        boolean isExist = checkGroupIsExist(groupName,managerId);
        if (isExist)
            return new ServiceData(false,null);  //MyEnum.GROUP_EXIST
        int effectCount;
        try{
            effectCount = groupDao.createGroup(groupName,managerId);    //创建群组影响的行数
        }catch (DataAccessException de){
            logger.error("创建群组时发生数据库错误："+de);
            throw new CreateGroupErrorException("创建群组(不带初始化参数)时发生错误！");
        }
        if (effectCount>0){//影响行数大于0，则说明插入成功
            /**查找群组，为了将群组id等信息返回给用户*/
            Group group = groupDao.queryByGroupNameManagerId(groupName,managerId);
            return new ServiceData(true,group);
        }
        else
            return new ServiceData(false,-1,null);
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
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return false;
        else
            return true;
    }

    @Override
    public boolean checkGroupIsExist(long groupId) {
        Group group = groupDao.queryById(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return false;
        else
            return true;
    }

    @Override
    public boolean isManagerOfThisGroup(long groupId, long managerId) {
        Group group = groupDao.queryById(groupId);
        if (DataFormatTransformUtil.isNullOrEmpty(group))
            return false;
        if (managerId == group.getManagerId())
            return true;
        return false;
    }

    /**
     * 删除群组并通知所有人
     * @param managerId
     * @param groupId
     * @return
     */
    @Override
    public ServiceData deleteGroup(long managerId, long groupId) throws DeleteGroupException{
        //确定管理员ID是否正确
        boolean isManagerOfGroup = isManagerOfThisGroup(groupId,managerId);
        if (!isManagerOfGroup)
            return new ServiceData(false,null);
        //删除群组，因为外键
        int effectCount;
        try{
            effectCount = groupDao.deleteGroup(managerId,groupId);
        }catch (DataAccessException de){
            logger.error("删除群组时数据库："+de);
            throw new DeleteGroupException("删除群组时发生SQLException！");
        }

        if (effectCount>0)
            return new ServiceData(true,null);
        else
            return new ServiceData(false,-1,null);
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
