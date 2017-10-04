package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.http.HttpURLConnPost;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Created by xiang on 2017/6/21.
 */
@Service
public class UserServiceImpl implements UserService{

    final static private String apikey = "fb0d88c9cd4a59d2e837fb3128d62409";

    final static private String url = "https://sms.yunpian.com/v2/sms/single_send.json";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDao userDao;

    @Override
    public UsersResponse getUserById(long id) {
        User user = userDao.queryById(id);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, user.getId() + ","
                    + user.getUserName() + "," + user.getMobile());
    }

    @Override
    public UsersResponse getUserByMobile(String mobile) {
        User user = userDao.queryByMobile(mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, user.getId() + ","
                    + user.getUserName() + "," + user.getMobile());
    }

    @Override
    public UsersResponse userLogin(String mobile, String password) {
        //queryPasswardByMobile()方法获取的user对象中包含了id、userName、password
        User user = userDao.queryPasswardByMobile(mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new UsersResponse(MyEnum.NEVER_REGISTER);
        else if (password.equals(user.getPassword()))
            return new UsersResponse(MyEnum.LOGIN_SUCCESS,
                    user.getUserName()+"("+user.getId()+")");
        else
            return new UsersResponse(MyEnum.PASSWORD_ERROR);
    }

    @Override
    public UsersResponse getAllUsers(int offset, int limit) {
        List<User> users = userDao.queryAll(offset,limit);
        if (DataFormatTransformUtil.isNullOrEmpty(users))
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        else
            return new UsersResponse(MyEnum.GET_USER_SUCCESS, users);
    }

    @Override
    public String getUsersName(Set<Long> userIdSet) {
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))
            return null;
        StringBuffer userNameString = new StringBuffer();
        for (long userId:userIdSet){
            String userName = userDao.queryUserNameById(userId);
            if (!DataFormatTransformUtil.isNullOrEmpty(userName))
                userNameString.append(userName+",");
        }
        return userNameString.toString();
    }

    @Override
    public UsersResponse sendLoginMessage(String mobile,HttpSession httpSession) {
        boolean isRegisted = checkUserByMobile(mobile);     //判断该手机号是否被注册过
        if (isRegisted)
            return new UsersResponse(MyEnum.USER_EXIST);
        else {//为新用户，可以执行发送短信
            int resCode = sendMessage(mobile,httpSession);
            if (resCode==0) //发送成功
                return new UsersResponse(MyEnum.MESSAGE_SEND_SUCCESS);
            else
                return new UsersResponse(MyEnum.MESSAGE_SEND_FAIL);
        }
    }

    @Override
    public UsersResponse sendResetMessage(String mobile, HttpSession httpSession) {
        boolean isRegisted = checkUserByMobile(mobile);     //判断该手机号是否被注册过
        if (isRegisted){        //已注册用户可以更改密码
            int resCode = sendMessage(mobile,httpSession);
            if (resCode ==0)
                return new UsersResponse(MyEnum.MESSAGE_SEND_SUCCESS);
            else
                return new UsersResponse(MyEnum.MESSAGE_SEND_FAIL);
        }
        else {//为新用户，不能发送短信
            return new UsersResponse(MyEnum.NEVER_REGISTER);

        }
    }

    @Override
    public UsersResponse justifyPassword(String mobile, String newPassword) {
        User user = userDao.queryByMobile(mobile);
        //没有找到用户
        if (DataFormatTransformUtil.isNullOrEmpty(user)){
            return new UsersResponse(MyEnum.USER_NOT_FOUND);
        }
        //修改密码
        int result = userDao.updateByMobile(mobile,newPassword);//result代表的是被影响的记录的个数
        if (result>0){
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_SUCCESS);
        }else {
            return new UsersResponse(MyEnum.MODIFY_PASSWORD_ERROR);
        }
    }

    @Override
    public UsersResponse userRegister(String userName, String mobile, String password) {
         int result = userDao.insertUser(userName,mobile,password);
         if (result <= 0)  //插入失败
             return new UsersResponse(MyEnum.ADD_USER_ERROR);
         else {  //插入成功
             User user = userDao.queryByMobile(mobile);
             return new UsersResponse(MyEnum.ADD_USER_SUCCESS,
                    user.getUserName() + "(" + user.getId() + ")");
         }
    }

    @Override
    public UsersResponse deleteUserById(long userId) {
        int result = userDao.deleteUserById(userId);
        if (result>0)
            return new UsersResponse(MyEnum.DELETE_USER_SUCCESS);
        else
            return new UsersResponse(MyEnum.DELETE_USER_FAIL);
    }

    /**
     * 检查mobile是否已经注册，若已经注册，返回true
     */
    private boolean checkUserByMobile(String mobile){
        User user = userDao.queryByMobile(mobile);
        if(DataFormatTransformUtil.isNullOrEmpty(user)){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 发送短信
     */
    private int sendMessage(String mobile,HttpSession httpSession){
        int num = createRandom();
        logger.info("您的验证码是" + num );
        String textCode = "您的验证码是" + num ;
        HttpURLConnPost post = new HttpURLConnPost();
        int resCode = post.doPost(url, apikey, mobile, textCode);
        /**判断发送是否成功*/
        if(resCode==0){//发送成功
            /**发送成功后，本地保存mobile和验证码*/
            httpSession.setAttribute("mobile", mobile);
            httpSession.setAttribute("textCode",""+num);
        }
        return resCode;
    }

    /**
     * 判断创建群组的管理者是否存在
     * @param managerId
     * @return
     */
    @Override
    public boolean isUserExist(long managerId){
        User user = userDao.queryById(managerId);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return false;
        else
            return true;
    }

    /**
     * 判断是否所有的用户都已注册
     * @param userIdSet
     * @return
     */
    @Override
    public boolean isAllUserExist(Set<Long> userIdSet){
        for (long userId:userIdSet){
            User user = userDao.queryById(userId);
            if (DataFormatTransformUtil.isNullOrEmpty(user))
                return false;
        }
        return true;
    }

    /**
     * 创建随即验证码
     */
    private int createRandom(){
        int a = (int)(Math.random()*(9999-1000+1))+1000;
        return a;
    }
}
