package com.astha.project.restful_webservices_udemy_part1.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//When user not found it is a 404 error and hence to return a 404 we need a ResponseStatus
@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
