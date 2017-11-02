package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class AddUserToGroupErrorException extends DataAccessException {

    public AddUserToGroupErrorException(String message) {
        super(message);
    }
}
