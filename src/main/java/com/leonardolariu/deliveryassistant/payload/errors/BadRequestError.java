package com.leonardolariu.deliveryassistant.payload.errors;

public class BadRequestError extends ApiError {
    public BadRequestError() {
        super(400, "Bad Request");
    }

    public BadRequestError(String message) {
        super(400, "Bad Request", message);
    }
}
