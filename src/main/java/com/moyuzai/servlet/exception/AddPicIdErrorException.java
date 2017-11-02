package com.moyuzai.servlet.exception;

import org.springframework.dao.DataAccessException;

public class AddPicIdErrorException extends DataAccessException {

    public AddPicIdErrorException(String message) {
        super(message);
    }
}
