package com.moyuzai.servlet.mina.model;

import com.moyuzai.servlet.exception.IoSessionIllegalException;

public interface NotifyUser {

    void notifyUsers() throws IoSessionIllegalException;

}
