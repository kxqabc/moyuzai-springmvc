package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class DeleteUserException extends DataAccessException {
    public DeleteUserException(String message) {
        super(message);
    }
}
