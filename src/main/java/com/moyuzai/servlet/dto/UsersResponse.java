package com.moyuzai.servlet.dto;

import com.moyuzai.servlet.enums.MyEnum;

/**
 * Created by kong on 17-6-22.
 */
public class UsersResponse<T> {
    private boolean state;
    private String message;
    private T identity;

    /**有对象返回的信息*/
    public UsersResponse(MyEnum enums , T identity) {
        this.state = enums.isState();
        this.message = enums.getStateInfo();
        this.identity = identity;
    }

    /**无对象返回的信息*/
    public UsersResponse(MyEnum enums) {
        state = enums.isState();
        message = enums.getStateInfo();
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
}
