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

    int queryGroupAmountByUserId(@Param("userId")long userId);

    int queryAmountInGroupByGroupId(@Param("groupId")long groupId);
//    只查询该用户所在的第一个groupId！=exGroupId的群组关系对象
    UserGroup queryAnotherGroupOfUser(@Param("exGroupId")long exGroupId,
                                 @Param("userId")long userId);

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
    //追加一条离线信息
    int addToOfflineText(@Param("text")String text,
                         @Param("userId") long userId,
                         @Param("groupId") long groupId);

    /**一次更新多条离线信息（属于同一用户下）*/
    int updateOfflineTextMulti(@Param("text") String text,
                          @Param("userId") long userId);
}
