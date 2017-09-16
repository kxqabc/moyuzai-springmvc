package com.moyuzai.servlet.dao;

import com.moyuzai.servlet.entity.Group;
import org.apache.ibatis.annotations.Param;

public interface GroupDao {

    Group queryById(@Param(value = "groupId")long groupId);

    Group queryByGroupNameManagerId(@Param(value = "groupName") String groupName,
                                    @Param(value = "managerId") long manageId);

    int createGroup(@Param(value = "groupName") String groupName,
                    @Param(value = "managerId") long manageId);

    int addGroupPic(@Param(value = "groupId")long groupId,
                    @Param(value = "picId")int picId);

    int deleteGroup(@Param(value = "managerId")long managerId,
                    @Param(value = "groupId")long groupId);

}
