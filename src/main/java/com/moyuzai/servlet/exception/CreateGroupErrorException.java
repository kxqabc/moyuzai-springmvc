package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class CreateGroupErrorException extends DataAccessException {

    public CreateGroupErrorException(String message) {
        super(message);
    }
}
