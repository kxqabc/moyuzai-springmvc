package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class MoyuzaiInnerErrorException extends DataAccessException {

    public MoyuzaiInnerErrorException(String message) {
        super(message);
    }
}
