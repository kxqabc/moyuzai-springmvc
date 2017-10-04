package com.moyuzai.servlet.controller;

import com.moyuzai.servlet.dto.GroupResponse;
import com.moyuzai.servlet.dto.UsersResponse;
import com.moyuzai.servlet.entity.Group;
import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.enums.MyEnum;
import com.moyuzai.servlet.exception.*;
import com.moyuzai.servlet.service.GroupService;
import com.moyuzai.servlet.service.MinaService;
import com.moyuzai.servlet.service.UserGroupService;
import com.moyuzai.servlet.util.DataFormatTransformUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import com.moyuzai.servlet.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kong on 17-6-19.
 */
@Controller
@RequestMapping("/")

public class MyController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

//    private static final int LIMIT = 20;     //默认返回结果集个数

    private static final int STEP = 20;      //每页的结果条数

    private static final int OFFSET = 0;     //结果集默认起始位置


    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private MinaService minaService;

    /**
     * 请求转发器，由于采用了标准的格式而没有使用restful风格url，试图通过请求参数“type”转发请求，
     * 同时也将各个模块的接口隐藏起来，不完全暴露给用户，用户只知道“固定网址”+“参数”，服务器通过
     * 参数确定应该由哪个接口接受请求。
     */
    @RequestMapping(value = "/Controller")
    public String transponder(@RequestParam("type") String type,HttpServletRequest request,
                              ModelAndView modelAndView){
//        logger.info("请求进入控制转发器。。");
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
        logger.info("按ID查询用户。。");
        logger.info("id:"+id);
        UsersResponse usersResponse = userService.getUserById(id);
        logger.info(usersResponse.toString());
        return usersResponse;
    }

    /**
     * 发送短信获取验证码
     */
    @ResponseBody
    @RequestMapping(value = "/sendLoginMessage")
    public UsersResponse sendLoginMessage(@RequestParam("mobile") String mobile,
                                          HttpSession httpSession){
        logger.info("发送短信获取验证码。。");
        logger.info("mobile:"+mobile);
        UsersResponse usersResponse = userService.sendLoginMessage(mobile,httpSession);
        logger.info(usersResponse.toString());
        return usersResponse;
    }

    /**
     * 查询所有用户(展示在page中)
     */
    @ResponseBody
    @RequestMapping(value = "/users")
    public UsersResponse queryAllUsers(@RequestParam("pageNum") int pageNum){
        logger.info("查询所有用户。。");
        logger.info("pageNum:"+pageNum);
        /**pageNum从1开始*/
        UsersResponse usersResponse = userService.getAllUsers(STEP*(pageNum-1), STEP*pageNum);
//        logger.info(usersResponse.toString());
        return usersResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/groups")
    public UsersResponse groups(@RequestParam("pageNum") int pageNum){
        logger.info("查询所有群组。。");
        /**pageNum从1开始*/
        UsersResponse usersResponse = groupService.getAllGroups(STEP*(pageNum-1), STEP*pageNum);
        return usersResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/userGroups")
    public UsersResponse userGroups(@RequestParam("pageNum") int pageNum){
        logger.info("查询所有关系表数据。。");
        /**pageNum从1开始*/
        UsersResponse usersResponse = userGroupService.getAll(STEP*(pageNum-1), STEP*pageNum);
        return usersResponse;
    }
    /**
     * 删除用户
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteUserById")
    public UsersResponse deleteUserById(@RequestParam(value = "userId")long userId){
        logger.info("删除用户。。");
        return userService.deleteUserById(userId);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteGroupById")
    public UsersResponse deleteGroupById(@RequestParam(value = "groupId")long groupId){
        logger.info("删除群组。。");
        return groupService.deleteGroup(groupId);
    }
    @ResponseBody
    @RequestMapping(value = "/deleteUserGroupById")
    public UsersResponse deleteUserGroupById(@RequestParam(value = "id")long id){
        logger.info("删除关系表中一条数据。。");
        return userGroupService.deleteUsersOfGroup(id);
    }

    /**
     * 根据手机号查询用户
     */
    @ResponseBody
    @RequestMapping(value = "/getByMobile")
    public UsersResponse queryUserByMobile(@RequestParam("mobile") String mobile,
                                           HttpSession httpSession){
        logger.info("按手机号码查询用户。。");
        logger.info("mobile:"+mobile);
        UsersResponse usersResponse = userService.getUserByMobile(mobile);
        logger.info(usersResponse.toString());
        return usersResponse;
    }
    @ResponseBody
    @RequestMapping(value = "/getUsersOfGroup")
    public UsersResponse getUsersOfGroup(@RequestParam("groupId") long groupId){
        logger.info("按群组ID查询用户。。");
        logger.info("groupId:"+groupId);
        UsersResponse usersResponse = userGroupService.getUsersOfGroup(groupId);
        logger.info(usersResponse.toString());
        return usersResponse;
    }

    /**
     * 核对验证码
     */
    @ResponseBody
    @RequestMapping(value = "/matchCode")
    public UsersResponse matchCode(@RequestParam("textCode") String textCode,
                                   HttpSession httpSession){
        logger.info("核对验证码。。");
        logger.info("textCode:"+textCode);
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
        logger.info("注册用户。。");
        logger.info("name:"+userName);
        logger.info("password:"+password);
        //从httpSession中获取用户之前填写的手机号
        String mobile = (String) httpSession.getAttribute("mobile");
        logger.info("mobile in httpSession:"+mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(mobile))
            return new UsersResponse(MyEnum.NO_MOBILE_FOUND); //获取不到手机号
        else{
            return userService.userRegister(userName,mobile,password);
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
        logger.info("用户登录。。");
        return userService.userLogin(mobile,password);
    }

    /**
     * 修改密码
     */
    @ResponseBody
    @RequestMapping(value = "/modifyPassword")
    public UsersResponse modifyPassword(@RequestParam(value = "password")String password,
                                        HttpSession httpSession){
        logger.info("修改密码。。");
        logger.info("password:"+password);
        String mobile = (String) httpSession.getAttribute("mobile");
        logger.info("mobile in httpSession:"+mobile);
        if (DataFormatTransformUtil.isNullOrEmpty(mobile))
            return paramNotFound();
        return userService.justifyPassword(mobile,password);
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
        logger.info("发送请求“重置密码”短信。。");
        return userService.sendResetMessage(mobile,httpSession);
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
        logger.info("创建群组（不包含群组头像等信息）。。");
        logger.info("groupName:"+groupName);
        logger.info("managerId:"+managerId);
        UsersResponse usersResponse = groupService.createGroup(groupName,managerId);
        logger.info(usersResponse.toString());
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
                                             @RequestParam("managerId")long managerId){
        logger.info("创建群组（包含群组头像、群组成员）。。");
        logger.info("users:"+users);
        logger.info("picId:"+picId);
        logger.info("groupName:"+groupName);
        logger.info("managerId:"+managerId);
        /**从String中提取用户ID信息*/
        Set<Long> userIdSet = null;
        try {
            userIdSet = DataFormatTransformUtil.StringToLongSet(users);
        }catch (NumberFormatException e){
            //字符串users中数字格式错误
            return new UsersResponse(MyEnum.STRING_FORMAT_REEOR);
        }
        if (DataFormatTransformUtil.isNullOrEmpty(userIdSet))      //users为空
            return paramNotFound();
        /**进行“事务”操作，若出现runtimeException则rollback，否则commit*/
        try {
            UsersResponse usersResponse = groupService.createGroupWithInit(userIdSet,managerId,groupName,picId);
            /**全部操作成功后，则通知所有组员已经被拉入该群组*/
            if (usersResponse.isState()){
                //除去管理员，不用通知他
                if (userIdSet.contains(managerId))
                    userIdSet.remove(managerId);
                //通知其他人
                minaService.notifyUserIsPulledIntoGroup(userIdSet,usersResponse);
            }
            return usersResponse;       //创建成功，返回成功信息的dto
        }catch (CreateGroupErrorException e1){      //创建不带初始化信息的群组失败
            return new UsersResponse(MyEnum.CREATE_GROUP_FAIL);
        }catch (AddPicIdErrorException e2){         //改写群组头像图片ID出错
            return new UsersResponse(MyEnum.ADD_GROUP_PIC_FAIL);
        }catch (AddUserToGroupErrorException e3){   //将用户添加入群组时出现错误
            return new UsersResponse(MyEnum.REMAIN_USERS);
        }catch (Exception e){                       //内部错误
            logger.info(e.getMessage());
            return new UsersResponse(MyEnum.INNER_REEOR);
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
        logger.info("根据群组ID查询群组信息。。");
        return groupService.getGroupById(groupId);
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
                                   @RequestParam(value = "groupId")long groupId){
        logger.info("申请加入群组。。");
        UsersResponse usersResponse = userGroupService.joinGroup(userId,groupId);
        if (usersResponse.isState()){
            //通知其他人有人加入该群组
            minaService.notifySomeJoined(groupId,userId);
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
                                          @RequestParam(value = "groupId")long groupId){
        logger.info("退出群组。。");
        return userGroupService.signoutFromGroup(userId,groupId);
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
                                      @RequestParam(value = "groupId")long groupId){
        logger.info("解散群组。。");
        List<Long> userIds = userGroupService.queryAllUserIdOfGroup(groupId);
        UsersResponse usersResponse = groupService.deleteGroup(managerId,groupId);
        if (usersResponse.isState()){
            //通知组内所有人该组解散
            Set<Long> userIdSet = new HashSet<>();
            userIdSet.addAll(userIds);
            userIdSet.remove(managerId);    //除去管理者，不通知他
            minaService.notifyUserGroupIsDisMissed(userIdSet,groupId);
        }
        //解散群组之前应该先记录组员
        return usersResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/changeGroupData")
    public UsersResponse changeGroupData(@RequestParam(value = "managerId")long managerId,
                                         @RequestParam(value = "groupId")long groupId,
                                         //这里必须用对象Integer，否则用int的话是基础类型，若为null不能转换，会报错！
                                         @RequestParam(value = "picId",required = false)Integer picId,
                                         @RequestParam(value = "groupName",required = false)String groupName,
                                         @RequestParam(value = "addUsers",required = false)String addUsers,
                                         @RequestParam(value = "minusUsers",required = false)String minusUsers){
        logger.info("修改群组资料。。");
        logger.info("picId:"+picId+",groupName:"+groupName+",users:"+addUsers+"/"+minusUsers);
        try {
            UsersResponse usersResponse = groupService.updateGroupDate(groupId,
                            managerId,picId,groupName,addUsers,minusUsers);
            //如果操作成功，通知群内其他所有人
            if (usersResponse.isState()){
                Map<String,Object> usersMap = (Map<String, Object>) usersResponse.getIdentity();
                if (usersMap.containsKey("unAffectedUsers")){
                    Group group = groupService.getGroupWithManName(groupId);
                    group.setAmount(userGroupService.getAmountInGroupById(groupId));
                    minaService.notifyUsersGroupMessageChange((Set<Long>) usersMap.get("unAffectedUsers"), group,addUsers);
                }
                if (usersMap.containsKey("addUsers"))
                    minaService.notifyUserIsPulledIntoGroup((Set<Long>) usersMap.get("addUsers"),groupId);
                if (usersMap.containsKey("minusUsers"))
                    minaService.notifyUsersIsKickout((Set<Long>) usersMap.get("minusUsers"),groupId);
            }
            usersResponse.setIdentity(null);
            return usersResponse;
        }catch (AddPicIdErrorException e1){
            return new UsersResponse(MyEnum.ADD_GROUP_PIC_FAIL);
        }catch (ChangeGroupNameException e2){
            return new UsersResponse(MyEnum.CHANG_GROUP_NAME_FAIL);
        }catch (AddUserToGroupErrorException e3){
            return new UsersResponse(MyEnum.REMAIN_USERS);
        }catch (DeleteUserException e4){
            return new UsersResponse(MyEnum.DELETE_USER_FAIL);
        }catch (NumberFormatException e5){
            return new UsersResponse(MyEnum.STRING_FORMAT_REEOR);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new UsersResponse(MyEnum.INNER_REEOR);
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
