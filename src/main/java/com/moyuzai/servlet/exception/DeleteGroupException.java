package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class DeleteGroupException extends DataAccessException{
    public DeleteGroupException(String message) {
        super(message);
    }
}
