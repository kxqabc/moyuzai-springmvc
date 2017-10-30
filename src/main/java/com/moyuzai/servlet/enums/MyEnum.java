package com.moyuzai.servlet.enums;

import com.sun.org.apache.bcel.internal.generic.FADD;
import com.sun.org.apache.bcel.internal.generic.FALOAD;

/**
 * Created by kong on 17-6-22.
 */
public enum MyEnum {
    GET_USER_SUCCESS(true,1,"查找用户成功！"),
    MODIFY_PASSWORD_SUCCESS(true,2,"密码更改成功!"),
    ADD_USER_SUCCESS(true,3,"用户注册成功！"),
    MESSAGE_SEND_SUCCESS(true,5,"验证码发送成功！"),
    TEXT_CODE_MATCH_SUCCESS(true,6,"验证码核对成功！"),
    LOGIN_SUCCESS(true,7,"登录成功！"),
    CREATE_GROUP_SUCCESS(true,8,"创建群组成功！"),
    JOIN_GROUP_SUCCESS(true,9,"加入群组成功！"),
    SIGNOUT_SUCCESS(true,10,""),
    DELETE_USER_SUCCESS(true,11,"删除用户成功！"),
    DISMISS_GROUP_SUCCESS(true,12,"解散群组成功！"),
    CHANGE_GROUP_PIC_SUCCESS(true,13,"修改群组头像成功！"),
    CHANGE_GROUP_NAME_SUCCESS(true,14,"修改群组名称成功！"),
    CHANGE_GROUP_DATE_SUCCESS(true,15,"修改群组信息成功！"),

    INNER_REEOR(false,0,"内部错误！"),
    USER_NOT_FOUND(false,-1,"找不到用户！"),
    MODIFY_PASSWORD_ERROR(false,-2,"密码更改失败!"),
    NO_MOBILE_FOUND(false,-3,"未找到注册手机号码！"),
    ADD_USER_ERROR(false,-4,"用户注册失败！"),
    PARAM_NOT_FOUND(false,-5,"请求参数错误或缺失！"),
    MESSAGE_SEND_FAIL(false,-6,"验证码发送失败！"),
    NO_TEXT_CODE(false,-7,"验证失败！会话可能已经过期。"),
    TEXT_CODE_MATCH_FAIL(false,-8,"验证码核对失败！"),
    LOGIN_FAIL(false,-9,"登录失败！"),
    NEVER_REGISTER(false,-10,"手机号未注册！"),
    GROUP_EXIST(false,-11,"创建失败！群组已存在。"),
    MANAGER_ERROR(false,-12,"管理者id出错，请核对用户信息！"),
    CREATE_GROUP_FAIL(false,-13,"创建失败！群组已存在。"),
    USERS_NOT_REGISTER(false,-14,"所添加的用户信息错误！"),
    ADD_GROUP_PIC_FAIL(false,-15,"添加头像失败！"),
    REMAIN_USERS(false,-16,"因部分用户添加入群组失败，使群组创建失败！"),
    GROUP_IS_NOT_EXIST(false,-17,"群组不存在！请核对数据。"),
    GROUP_USER_INFO_ERROR(false,-18,"加入群组失败，请确定用户、群组信息无误！"),
    IS_JOINED(false,-19,"加入群组失败，该用户已经加入过该群组！请不要重复添加！"),
    JOIN_GROUP_FAIL(false,-20,"加入群组失败！"),
    NOT_IN_GROUP_ERROR(false,-21,"您不在该群里！请再次核对信息。"),
    SIGNOUT_GROUP_FAIL(false,-22,""),
    DELETE_USER_FAIL(false,-23,"删除用户失败！"),
    DISMISS_GROUP_FAIL(false,-24,"解散群组失败！"),
    USER_EXIST(false,-25,"用户已经存在！"),
    PASSWORD_ERROR(false,-26,"密码错误！"),
    NOT_THE_MANAGER_OF_THIS_GROUP(false,-27,"对不起，您不是该群组的管理员！操作无效！"),
    CHANG_GROUP_NAME_FAIL(false,-28,"修改群组名称失败！"),
    STRING_FORMAT_REEOR(false,-29,"字符串转数组出现错误！请核对数据。"),
    DATABASE_CLASS_ERROR(false,-30,"查询数据库数据不符合期望类型！");

    private boolean state;
    private int stateNum;
    private String stateInfo;

    MyEnum(boolean state, int stateNum, String stateInfo) {
        this.state = state;
        this.stateNum = stateNum;
        this.stateInfo = stateInfo;
    }

    public boolean isState() {
        return state;
    }

    public int getStateNum() {
        return stateNum;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setStateNum(int stateNum) {
        this.stateNum = stateNum;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
