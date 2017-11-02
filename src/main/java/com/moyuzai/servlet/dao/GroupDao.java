package com.moyuzai.servlet.dao;

import com.moyuzai.servlet.entity.Group;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;
import java.util.List;

public interface GroupDao {

    List<Group> queryAll(@Param(value = "offset")int offset,
                        @Param(value = "limit")int limit);

    Group queryById(@Param(value = "groupId")long groupId);

    Group queryByGroupNameManagerId(@Param(value = "groupName") String groupName,
                                    @Param(value = "managerId") long manageId);

    Group queryGroupWithManNameById(@Param(value = "groupId")long groupId);

    int createGroup(@Param(value = "groupName") String groupName,
                    @Param(value = "managerId") long manageId)throws DataAccessException;

    int updateGroupPic(@Param(value = "groupId")long groupId,
                    @Param(value = "picId")int picId);

    int updateGroupName(@Param(value = "groupId")long groupId,
                        @Param(value = "groupName")String groupName);

    int deleteGroup(@Param(value = "managerId")long managerId,
                    @Param(value = "groupId")long groupId)throws SQLException;

    int deleteGroupById(@Param(value = "groupId")long groupId);

}
