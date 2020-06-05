package com.leonardolariu.deliveryassistant.payload.errors;

public class ForbiddenError extends ApiError {
    public ForbiddenError() {
        super(403, "Forbidden");
    }

    public ForbiddenError(String message) {
        super(403, "Forbidden", message);
    }
}