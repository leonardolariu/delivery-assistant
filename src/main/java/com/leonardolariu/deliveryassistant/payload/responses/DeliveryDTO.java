package com.leonardolariu.deliveryassistant.payload.responses;

import com.leonardolariu.deliveryassistant.models.EDeliveryStatus;
import com.leonardolariu.deliveryassistant.services.utils.Centroid;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class DeliveryDTO {
    private Long id;
    private String date;
    private EDeliveryStatus status;
    private int estimatedDriversCount;
    private int actualDriversCount;
    private int packagesCount;
    private double minimumDistanceToCover;
    private Centroid mapCenter;
    private Set<Route> routes;
}
