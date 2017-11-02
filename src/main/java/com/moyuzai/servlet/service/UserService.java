package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import org.springframework.dao.DataAccessException;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by xiang on 2017/6/21.
 */
public interface UserService {
    /**查询*/
    ServiceData getUserById(long id);

    ServiceData getUserByMobile(String mobile);

    ServiceData userLogin(String mobile,String password);

    ServiceData getAllUsers(int offset, int limit);

    ServiceData getUsersName(Set<Long> userIdSet);

    ServiceData sendLoginMessage(String mobile,HttpSession httpSession);

    ServiceData sendResetMessage(String mobile,HttpSession httpSession);
    /**修改*/
    ServiceData justifyPassword(String mobile, String newPassword)throws DataAccessException;

    /**插入*/
    ServiceData userRegister(String userName,String mobile,String password) throws DataAccessException;

    ServiceData deleteUserById(long userId);      //需不需要删除关系表中的信息？
    /**工具方法*/
    boolean isUserExist(long managerId);

    boolean isAllUserExist(Set<Long> userIdSet);

}
