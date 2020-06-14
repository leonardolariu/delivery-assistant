package com.leonardolariu.deliveryassistant.payload.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DeliveriesData {
    private List<DeliveryDTO> deliveries;
}
