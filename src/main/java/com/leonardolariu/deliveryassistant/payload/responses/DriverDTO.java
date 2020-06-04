package com.leonardolariu.deliveryassistant.payload.responses;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DriverDTO {
    private Long id;
    private String name;
    private String email;
    private String since;
}
