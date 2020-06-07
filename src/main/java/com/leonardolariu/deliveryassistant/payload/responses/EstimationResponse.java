package com.leonardolariu.deliveryassistant.payload.responses;

import lombok.Getter;

@Getter
public class EstimationResponse {
    private int estimatedDriversCount;

    public EstimationResponse(int estimatedDriversCount) {
        this.estimatedDriversCount = estimatedDriversCount;
    }
}
