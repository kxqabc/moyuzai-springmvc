package com.moyuzai.servlet.controller;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.ServiceData;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.*;
import com.moyuzai.servlet.mina.core.ServerHandler;
import com.moyuzai.servlet.mina.model.*;
import com.moyuzai.servlet.service.*;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by kong on 17-6-19.
 */

@Controller
@RequestMapping("/")
public class MyController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int STEP = 20;      //每页的结果条数

    @Autowired
    private ServiceProxy serviceProxy;

    /**
     * 请求转发器，由于采用了标准的格式而没有使用restful风格url，试图通过请求参数“type”转发请求，
     * 同时也将各个模块的接口隐藏起来，不完全暴露给用户，用户只知道“固定网址”+“参数”，服务器通过
     * 参数确定应该由哪个接口接受请求。
     */
    @RequestMapping(value = "/Controller")
    public String transponder(@RequestParam("type") String type,HttpServletRequest request,
                              ModelAndView modelAndView){
        switch (type){
            case "users":return "forward:/users";
            case "groups":return "forward:/groups";
            case "userGroups":return "forward:/userGroups";
            case "getUser":return "forward:/getById";
            case "deleteUser":return "forward:/deleteUserById";
            case "deleteGroup":return "forward:/deleteGroupById";
            case "deleteUserGroup":return "forward:/deleteUserGroupById";
            case "GETUSER":return "forward:/getByMobile";
            case "GETUSERS":return "forward:/getUsersOfGroup";
            case "GETSME":return "forward:/sendLoginMessage";
            case "MATCH":return "forward:/matchCode";
            case "REGISTER":return "forward:/registerUser";
            case "LOGIN":return "forward:/login";
            case "RESETSME":return "forward:/sendResetPasswordMessage";
            case "RESET":return "forward:/modifyPassword";
            case "CREATEGROUP":return "forward:/createGroup";
            case "INITGROUP":return "forward:/createGroupWithInit";
            case "GETGROUP":return "forward:/getGroupById";
            case "JOINGROUP":return "forward:/joinGroup";
            case "SIGNOUTGROUP":return "forward:/signoutFromGroup";
            case "DISMISSGROUP":return "forward:/dismissGroup";
            case "CHANGEPIC":return "forward:/changeGroupPic";
            case "CHANGEGROUPNAME":return "forward:/changeGroupName";
            case "ADDUSER":return "forward:/addUserToGroup";
            case "CHGROUPDATA":return "forward:/changeGroupData";
            default:return "forward:/paramNotFound";
        }
    }

    /**
     * 根据用户ID查询
     */
    @ResponseBody
    @RequestMapping(value = "/getById")
    public UsersResponse queryUserById(@RequestParam("id") long id){
        UsersResponse usersResponse = null;
        try {
            usersResponse = serviceProxy.getUserById(id);
        } catch (DataClassErrorException e) {
            e.printStackTrace();
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
        return usersResponse;
    }

    /**
     * 发送短信获取验证码
     */
    @ResponseBody
    @RequestMapping(value = "/sendLoginMessage")
    public UsersResponse sendLoginMessage(@RequestParam("mobile") String mobile,
                                          HttpSession httpSession){
        UsersResponse usersResponse = serviceProxy.sendLoginMessage(mobile,httpSession);
        return usersResponse;
    }

    /**
     * 查询所有用户(展示在page中)
     */
    @ResponseBody
    @RequestMapping(value = "/users")
    public UsersResponse queryAllUsers(@RequestParam("pageNum") int pageNum){
        //pageNum从1开始
        UsersResponse usersResponse = serviceProxy.getAllUsers(STEP*(pageNum-1), STEP*pageNum);
        return usersResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/groups")
    public UsersResponse groups(@RequestParam("pageNum") int pageNum){
        //pageNum从1开始
        UsersResponse usersResponse = serviceProxy.getAllGroups(STEP*(pageNum-1), STEP*pageNum);
        return usersResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/userGroups")
    public UsersResponse userGroups(@RequestParam("pageNum") int pageNum){
        //pageNum从1开始
        UsersResponse usersResponse = serviceProxy.getAll(STEP*(pageNum-1), STEP*pageNum);
        return usersResponse;
    }

    /**
     * 删除用户(数据库控制界面接口)
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteUserById")
    public UsersResponse deleteUserById(@RequestParam(value = "userId")long userId){
        return serviceProxy.deleteUserById(userId);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteGroupById")
    public UsersResponse deleteGroupById(@RequestParam(value = "groupId")long groupId){
        return serviceProxy.deleteGroup(groupId);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteUserGroupById" )
    public UsersResponse deleteUserGroupById(@RequestParam(value = "id")long id){
        return serviceProxy.deleteUserOfGroup(id);
    }

    /**
     * 根据手机号查询用户
     */
    @ResponseBody
    @RequestMapping(value = "/getByMobile")
    public UsersResponse queryUserByMobile(@RequestParam("mobile") String mobile,
                                           HttpSession httpSession){
        UsersResponse usersResponse;
        try {
            usersResponse = serviceProxy.getUserByMobile(mobile);
        } catch (DataClassErrorException e) {
            e.printStackTrace();
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
        return usersResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getUsersOfGroup")
    public UsersResponse getUsersOfGroup(@RequestParam("groupId") long groupId){
        UsersResponse usersResponse = serviceProxy.getUsersOfGroup(groupId);
        return usersResponse;
    }

    /**
     * 核对验证码
     */
    @ResponseBody
    @RequestMapping(value = "/matchCode")
    public UsersResponse matchCode(@RequestParam("textCode") String textCode,
                                   HttpSession httpSession){
        String originCode = (String) httpSession.getAttribute("textCode");
        if (DataFormatTransformUtil.isNullOrEmpty(originCode)){
            return new UsersResponse(MyEnum.NO_TEXT_CODE);
        }else {
            if (originCode.equals(textCode))
                return new UsersResponse(MyEnum.TEXT_CODE_MATCH_SUCCESS);
            else
                return new UsersResponse(MyEnum.TEXT_CODE_MATCH_FAIL);
        }
    }

    /**
     *注册用户
     */
    @ResponseBody
    @RequestMapping(value = "/registerUser")
    public UsersResponse registerUser(@RequestParam(value = "name")String userName,
                                      @RequestParam(value = "password")String password,
                                      HttpSession httpSession){
        //从httpSession中获取用户之前填写的手机号
        String mobile = (String) httpSession.getAttribute("mobile");
        if (DataFormatTransformUtil.isNullOrEmpty(mobile))  //获取不到手机号
            return new UsersResponse(MyEnum.NO_MOBILE_FOUND);
        else{
            try {
                return serviceProxy.userRegister(userName,mobile,password);
            } catch (DataClassErrorException e) {
                logger.error("注册用户时发生异常："+e);
                return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
            } catch (DataAccessException de){
                logger.error("注册用户时发生异常："+de);
                return new UsersResponse(MyEnum.SQLEXCEPTION);
            }
        }
    }

    /**
     * 登录
     * @param mobile
     * @param password
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login")
    public UsersResponse login(@RequestParam("mobile")String mobile,
                               @RequestParam("password")String password){
        UsersResponse usersResponse;
        usersResponse =  serviceProxy.userLogin(mobile,password);
        return usersResponse;
    }

    /**
     * 修改密码
     */
    @ResponseBody
    @RequestMapping(value = "/modifyPassword")
    public UsersResponse modifyPassword(@RequestParam(value = "password")String password,
                                        HttpSession httpSession){
        String mobile = (String) httpSession.getAttribute("mobile");
        UsersResponse usersResponse;
        if (DataFormatTransformUtil.isNullOrEmpty(mobile))
            return paramNotFound();
        try {
            usersResponse = serviceProxy.justifyPassword(mobile,password);
        }catch (DataAccessException de){
            logger.info("更改密码时发生SQL异常："+de);
            return new UsersResponse(MyEnum.SQLEXCEPTION);
        }
        return usersResponse;
    }

    /**
     * 发送请求“重置密码”短信
     * @param mobile
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendResetPasswordMessage")
    public UsersResponse sendResetPasswordMessage(@RequestParam("mobile") String mobile,
                                              HttpSession httpSession){
        UsersResponse usersResponse = serviceProxy.sendResetMessage(mobile,httpSession);
        return usersResponse;
    }

    /**
     * 创建群组（不包含群组头像等信息）
     * @param groupName
     * @param managerId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createGroup")
    public UsersResponse createGroup(@RequestParam("groupName")String groupName,
                            @RequestParam("managerId")long managerId){
        UsersResponse usersResponse;
        try{
            usersResponse = serviceProxy.createGroup(groupName,managerId);
        }catch (CreateGroupErrorException e1){
            logger.error("创建群组时发生CreateGroupErrorException："+e1.getMessage());
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
        }catch (AddUserToGroupErrorException e2){
            logger.error("创建群组时发生AddUserToGroupErrorException："+e2.getMessage());
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
        }catch (DataClassErrorException e3){
            logger.error("创建群组时发生DataClassErrorException："+e3.getMessage());
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
        return usersResponse;
    }

    /**
     * 创建群组（包含群组头像、群组成员，用到了“事务管理” )
     * @param users
     * @param picId
     * @param groupName
     * @param managerId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createGroupWithInit")
    public UsersResponse createGroupWithInit(@RequestParam("users")String users,
                                             @RequestParam("picId")int picId,
                                             @RequestParam("groupName")String groupName,
                                             @RequestParam("managerId")long managerId) throws TargetLostException, IoSessionIllegalException {
        /**从String中提取用户ID信息*/
        Set<Long> userIdSet;
        try {
            userIdSet = DataFormatTransformUtil.StringToLongSet(users);
        }catch (NumberFormatException e){
            //字符串users中数字格式错误
            return new UsersResponse(MyEnum.STRING_FORMAT_REEOR);
        }
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))      //users为空
            return paramNotFound();
        /**进行“事务”操作，若出现runtimeException则rollback，否则commit*/
        //别忘记加入管理员
        if (!userIdSet.contains(managerId))
            userIdSet.add(managerId);
        try {
            UsersResponse usersResponse = serviceProxy.createGroupWithInit(userIdSet,managerId,groupName,picId);
            //全部操作成功后，则通知所有组员已经被拉入该群组
            /**考虑是否开新线程？？？？？*/
            if (usersResponse.isState()){
                //准备通知前的参数
                String groupInfo = (String)usersResponse.getIdentity();     //从dto中取出群组信息：ID和名称
                long groupId = Long.parseLong(groupInfo.substring(groupInfo.indexOf("(") + 1, groupInfo.indexOf(")")));     //从群组信息中获取群组ID
                if (DataFormatTransformUtil.isNullOrEmpty(groupId)){
                    logger.warn("创建群组（含初始化参数）获取通知群组ID时出错！");
                    return usersResponse;
                }
                //调用mina进行通知,并且去除管理者，不需要通知管理者
                if (userIdSet.contains(managerId))
                    userIdSet.remove(managerId);
                serviceProxy.notifyUserIsPulledIntoGroup(userIdSet,groupId);
            }
            return usersResponse;       //创建成功，返回成功信息的dto
        }catch (CreateGroupErrorException e1){      //创建不带初始化信息的群组失败
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
        }catch (AddPicIdErrorException e2){         //改写群组头像图片ID出错
            return new UsersResponse(MyEnum.ADD_GROUP_PIC_FAIL);
        }catch (AddUserToGroupErrorException e3){   //将用户添加入群组时出现错误
            return new UsersResponse(MyEnum.REMAIN_USERS);
        }  catch (DataClassErrorException e) {
            e.printStackTrace();
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
    }

    /**
     * 根据群组ID查询群组信息
     * @param groupId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGroupById")
    public GroupResponse getGroupById(@RequestParam(value = "groupId")long groupId){
        GroupResponse groupResponse = null;
        try {
            groupResponse = serviceProxy.getGroupById(groupId);
        } catch (DataClassErrorException e) {
            logger.error("根据群组ID查询群组信息时发生异常："+e.getMessage());
            return new GroupResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
        return groupResponse;
    }

    /**
     * 申请加入群组
     * @param userId
     * @param groupId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/joinGroup")
    public UsersResponse joinGroup(@RequestParam(value = "userId")long userId,
                                   @RequestParam(value = "groupId")long groupId) throws IoSessionIllegalException {
        UsersResponse usersResponse;
        try{
            usersResponse = serviceProxy.joinGroup(userId,groupId);
        }catch (AddUserToGroupErrorException e1){
            logger.error("申请加入群组时出现异常："+e1.getMessage());
            return new UsersResponse(MyEnum.JOIN_GROUP_FAIL);
        }
        if (usersResponse.isState()){
            //通知其他人有人加入该群组
            serviceProxy.notifySomeJoined(userId,groupId);

        }
        return usersResponse;
    }

    /**
     * 主动退出群组
     * @param userId
     * @param groupId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/signoutFromGroup")
    public UsersResponse signoutFromGroup(@RequestParam(value = "userId")long userId,
                                          @RequestParam(value = "groupId")long groupId) throws DataClassErrorException, IoSessionIllegalException, TargetLostException {
        UsersResponse usersResponse;
        try{
            usersResponse = serviceProxy.signoutFromGroup(userId,groupId);
            if(usersResponse.isState()){
                serviceProxy.notifySomeoneQuit(userId,groupId);
            }
        }catch (DeleteUserException e1){
            logger.error("退出群组："+groupId+"时出现异常DeleteUserException："+e1.getMessage());
            MyEnum.SIGNOUT_GROUP_FAIL.setStateInfo("退出群"+groupId+"失败！");
            return new UsersResponse(MyEnum.SIGNOUT_GROUP_FAIL);
        }
        return usersResponse;
    }

    /**
     * 管理员解散群组
     * @param managerId
     * @param groupId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/dismissGroup")
    public UsersResponse dismissGroup(@RequestParam(value = "managerId")long managerId,
                                      @RequestParam(value = "groupId")long groupId) throws IoSessionIllegalException {
        //解散群组前首先获取所有组员
        UsersResponse getUsersResponse = serviceProxy.queryAllUserIdOfGroup(groupId);
        List<Long> userIds = null;
        if (!DataFormatTransformUtil.isNullOrEmpty(getUsersResponse.getIdentity()))
            userIds = (List<Long>) getUsersResponse.getIdentity();
        UsersResponse deleteGroupResponse;
        try {
            deleteGroupResponse = serviceProxy.deleteGroup(managerId,groupId);
        }catch (DeleteGroupException e1){
            logger.error("解散群组时发生异常："+e1.getMessage());
            deleteGroupResponse = new UsersResponse(MyEnum.DISMISS_GROUP_FAIL);
        }

        if (deleteGroupResponse.isState()){
            //通知组内所有人该组解散
            Set<Long> userIdSet = new HashSet<>();
            userIdSet.addAll(userIds);
            userIdSet.remove(managerId);    //除去管理者，不通知他
            if (DataFormatTransformUtil.isNullOrEmpty(userIds))
                return deleteGroupResponse;
            serviceProxy.notifyUserGroupIsDisMissed(userIdSet,groupId);
        }
        //解散群组之前应该先记录组员
        return deleteGroupResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/changeGroupData")
    public UsersResponse changeGroupData(@RequestParam(value = "managerId")long managerId,
                                         @RequestParam(value = "groupId")long groupId,
                                         //这里必须用对象Integer，否则用int的话是基础类型，若为null不能转换，会报错！
                                         @RequestParam(value = "picId",required = false)Integer picId,
                                         @RequestParam(value = "groupName",required = false)String groupName,
                                         @RequestParam(value = "addUsers",required = false)String addUsers,
                                         @RequestParam(value = "minusUsers",required = false)String minusUsers) throws IoSessionIllegalException, TargetLostException {
        try {
            UsersResponse usersResponse = serviceProxy.updateGroupDate(groupId,
                            managerId,picId,groupName,addUsers,minusUsers);
            //如果操作成功，通知群内其他所有人
            if (usersResponse.isState()){
                //usersMap中保存了需要被通知的人群的信息
                Map<String,Object> usersMap = (Map<String, Object>) usersResponse.getIdentity();
                GroupResponse groupResponse = serviceProxy.getGroupWithMoreDetail(groupId);
                Group group = groupResponse.getGroup();
                //在更改群组信息中不受影响的人群，即没有被拉入也没被踢出的人
                if (usersMap.containsKey("unAffectedUsers")){
                    serviceProxy.notifyUsersGroupMessageChange((Set<Long>) usersMap.get("unAffectedUsers"),group,addUsers);
                }
                //被新拉入群组的人
                if (usersMap.containsKey("addUsers")){
                    serviceProxy.notifyUserIsPulledIntoGroup((Set<Long>) usersMap.get("addUsers"),groupId);
                }
                //被踢出群组的人
                if (usersMap.containsKey("minusUsers")){
                    serviceProxy.notifyUsersIsKickout((Set<Long>) usersMap.get("minusUsers"),groupId);
                }
            }
            usersResponse.setIdentity(null);
            return usersResponse;
        }catch (AddPicIdErrorException e1){
            logger.error("更改群组头像时出现异常："+e1);
            return new UsersResponse(MyEnum.ADD_GROUP_PIC_FAIL);
        }catch (ChangeGroupNameException e2){
            logger.error("修改就群组名称时出现异常："+e2);
            return new UsersResponse(MyEnum.CHANG_GROUP_NAME_FAIL);
        }catch (AddUserToGroupErrorException e3){
            logger.error("将用户添加到群组时出现异常："+e3);
            return new UsersResponse(MyEnum.REMAIN_USERS);
        }catch (DeleteUserException e4){
            logger.error("将用户踢出群组时出现异常："+e4);
            return new UsersResponse(MyEnum.DELETE_USER_FAIL);
        }catch (NumberFormatException e5){
            logger.error("数字转换时出现异常："+e5);
            return new UsersResponse(MyEnum.STRING_FORMAT_REEOR);
        } catch (DataClassErrorException e7) {
            logger.error("修改群组资料时出现异常DataClassErrorException："+e7);
            return new UsersResponse(MyEnum.DATABASE_CLASS_ERROR);
        }
    }

    /**
     * 获取不到参数
     */
    @ResponseBody
    @RequestMapping(value = "/paramNotFound")
    public UsersResponse paramNotFound(){
        return new UsersResponse(MyEnum.PARAM_NOT_FOUND);
    }

}
