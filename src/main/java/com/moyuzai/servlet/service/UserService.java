package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Created by xiang on 2017/6/21.
 */
public interface UserService {
    /**查询*/
    UsersResponse getUserById(long id);

    UsersResponse getUserByMobile(String mobile);

    UsersResponse userLogin(String mobile,String password);

    UsersResponse getAllUsers(int offset, int limit);

    String getUsersName(Set<Long> userIdSet);

    UsersResponse sendLoginMessage(String mobile,HttpSession httpSession);

    UsersResponse sendResetMessage(String mobile,HttpSession httpSession);
    /**修改*/
    UsersResponse justifyPassword(String mobile, String newPassword);

    /**插入*/
    UsersResponse userRegister(String userName,String mobile,String password);

    UsersResponse deleteUserById(long userId);      //需不需要删除关系表中的信息？
    /**工具方法*/
    boolean isUserExist(long managerId);

    boolean isAllUserExist(Set<Long> userIdSet);

}
