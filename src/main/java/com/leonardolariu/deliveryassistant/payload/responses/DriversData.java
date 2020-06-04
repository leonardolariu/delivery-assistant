package com.leonardolariu.deliveryassistant.payload.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class DriversData {
    private int driversCount;
    private Set<DriverDTO> drivers;
}
