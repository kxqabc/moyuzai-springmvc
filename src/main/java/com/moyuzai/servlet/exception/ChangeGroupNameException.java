package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class ChangeGroupNameException extends DataAccessException {
    public ChangeGroupNameException(String message) {
        super(message);
    }
}
