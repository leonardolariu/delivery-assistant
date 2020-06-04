package com.leonardolariu.deliveryassistant.payload.errors;

public class NotFoundError extends ApiError {
    public NotFoundError() {
        super(404, "Not Found");
    }

    public NotFoundError(String message) {
        super(400, "Not Found", message);
    }
}
