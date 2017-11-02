package com.moyuzai.servlet.service;

import com.moyuzai.servlet.dao.UserDao;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.http.HttpURLConnPost;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
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
    public ServiceData getUserById(long id) {
        User user = userDao.queryById(id);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,user);
    }

    @Override
    public ServiceData getUserByMobile(String mobile) {
        User user = userDao.queryByMobile(mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,user);
    }

    @Override
    public ServiceData userLogin(String mobile, String password) {
        //queryPasswardByMobile()方法获取的user对象中包含了id、userName、password
        User user = userDao.queryPasswardByMobile(mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(user))
            return new ServiceData(false,null);
        else{
            if (password.equals(user.getPassword())){
                String result = user.getUserName()+"("+user.getId()+")";
                return new ServiceData(true,result);
            }else
                return new ServiceData(false,-1,null);
        }
    }

    @Override
    public ServiceData getAllUsers(int offset, int limit) {
        List<User> users = userDao.queryAll(offset,limit);
        if (DataFormatTransformUtil.isNullOrEmpty(users))
            return new ServiceData(false,null);
        else
            return new ServiceData(true,users);
    }

    @Override
    public ServiceData getUsersName(Set<Long> userIdSet) {
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))
            return new ServiceData(false,null);
        StringBuffer userNameString = new StringBuffer();
        for (long userId:userIdSet){
            String userName = userDao.queryUserNameById(userId);
            if (!DataFormatTransformUtil.isNullOrEmpty(userName))
                userNameString.append(userName+",");
        }
        return new ServiceData(true,userNameString.toString());
    }

    @Override
    public ServiceData sendLoginMessage(String mobile,HttpSession httpSession) {
        boolean isRegisted = checkUserByMobile(mobile);     //判断该手机号是否被注册过
        if (isRegisted)
            return new ServiceData(false,null);
        else {
            //为新用户，可以执行发送短信
            int resCode = sendMessage(mobile,httpSession);
            if (resCode==0) //发送成功
                return new ServiceData(true,null);
            else
                return new ServiceData(false,-1,null);
        }
    }

    @Override
    public ServiceData sendResetMessage(String mobile, HttpSession httpSession) {
        boolean isRegisted = checkUserByMobile(mobile);     //判断该手机号是否被注册过
        if (isRegisted){        //已注册用户可以更改密码
            int resCode = sendMessage(mobile,httpSession);
            if (resCode ==0)
                return new ServiceData(true,null);
            else
                return new ServiceData(false,-1,null);
        }
        else {//为新用户，不能发送短信
            return new ServiceData(false,null);
        }
    }

    @Override
    public ServiceData justifyPassword(String mobile, String newPassword) throws DataAccessException{
        User user = userDao.queryByMobile(mobile);
        //没有找到用户
        if (DataFormatTransformUtil.isNullOrEmpty(user)){
            return new ServiceData(false,null);
        }
        //修改密码
        int result;
        try {
            result = userDao.updateByMobile(mobile,newPassword);//result代表的是被影响的记录的个数
        }catch (DataAccessException de){
            logger.error("更改密码时数据库发生异常："+de);
            throw de;
        }
        if (result>0){
            return new ServiceData(true,null);
        }else {
            return new ServiceData(false,-1,null);
        }
    }

    @Override
    public ServiceData userRegister(String userName, String mobile, String password) throws DataAccessException {
        int result;
        try {
            result = userDao.insertUser(userName,mobile,password);
        }catch (DataAccessException de){
            logger.info("用户注册插入数据库时发生异常："+de.getMessage());
            throw de;
        }
         if (result <= 0)  //插入失败
             return new ServiceData(false,null);
         else {  //插入成功
             User user = userDao.queryByMobile(mobile);
             return new ServiceData(true,user);
         }
    }

    @Override
    public ServiceData deleteUserById(long userId) {
        int result = userDao.deleteUserById(userId);
        if (result>0)
            return new ServiceData(true,null);
        else
            return new ServiceData(false,null);
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
