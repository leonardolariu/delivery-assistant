package com.leonardolariu.deliveryassistant.payload.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DriversData {
    private int driversCount;
    private List<DriverDTO> drivers;
}
