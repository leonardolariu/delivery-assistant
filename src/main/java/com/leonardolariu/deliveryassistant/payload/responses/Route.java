package com.leonardolariu.deliveryassistant.payload.responses;

import com.leonardolariu.deliveryassistant.services.utils.Package;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class Route {
    private List<Package> packages;
}
