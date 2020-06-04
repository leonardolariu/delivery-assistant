package com.leonardolariu.deliveryassistant.payload.errors;

import lombok.Getter;

@Getter
public class ApiException extends Exception {
    private int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }
}
