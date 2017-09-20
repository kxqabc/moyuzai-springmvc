package com.moyuzai.servlet.dao;

import com.moyuzai.servlet.entity.UserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserGroupDao {
    /**查询关系表*/

    List<UserGroup> queryAll(@Param(value = "offset")int offset,
                             @Param(value = "limit")int limit);

    UserGroup queryUserGroup(@Param("groupId")long groupId,
                             @Param("userId")long userId);

    int queryAmountInGroupByGroupId(@Param("groupId")long groupId);

    /**新增一个关系表内容*/
    int saveUserGroup(@Param("groupId")long groupId,
                      @Param("userId")long userId);

    /**去除一个关系表内容*/
    int deleteUserGroup(@Param("groupId")long groupId,
                        @Param("userId")long userId);

    int deleteUserGroupById(@Param("id")long id);

    List<Long> queryAllUserIdOfGroup(@Param("groupId")long groupId);

    List<String> getOfflineTextMulti(@Param("userId") long userId);

    String getOfflineTextUnique(@Param("userId")long userId,
                                @Param("groupId")long groupId);

    /**一次更新一条离线信息*/
    int updateOfflineTextUnique(@Param("text") String text,
                          @Param("userId") long userId,
                          @Param("groupId") long groupId);
    /**一次更新多条离线信息（属于同一用户下）*/
    int updateOfflineTextMulti(@Param("text") String text,
                          @Param("userId") long userId);
}
