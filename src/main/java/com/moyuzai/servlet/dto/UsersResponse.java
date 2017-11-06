package com.moyuzai.servlet.dto;

import com.moyuzai.servlet.enums.MyEnum;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kong on 17-6-22.
 */
public class UsersResponse<T> {

    private boolean state;

    private String message;

    private T identity;

    private String time;

    public UsersResponse() {
    }

    /**有对象返回的信息*/
    public UsersResponse(MyEnum enums , T identity) {
        this.state = enums.isState();
        this.message = enums.getStateInfo();
        this.identity = identity;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = dateFormat.format(new Date());
    }

    /**无对象返回的信息*/
    public UsersResponse(MyEnum enums) {
        state = enums.isState();
        message = enums.getStateInfo();
    }

    public void setFromEnum(MyEnum myEnum){
        state = myEnum.isState();
        message = myEnum.getStateInfo();
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getIdentity() {
        return identity;
    }

    public void setIdentity(T identity) {
        this.identity = identity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UsersResponse{" +
                "state=" + state +
                ", message='" + message + '\'' +
                ", identity=" + identity +
                ", time='" + time + '\'' +
                '}';
    }
}
