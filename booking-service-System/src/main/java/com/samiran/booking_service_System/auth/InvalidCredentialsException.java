package com.samiran.booking_service_System.auth;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
    public InvalidCredentialsException(String message,Throwable cause){
        super(message,cause);
    }
}
