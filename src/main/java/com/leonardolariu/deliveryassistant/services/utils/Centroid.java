package com.leonardolariu.deliveryassistant.services.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Centroid {
    private Double xCoordinate;
    private Double yCoordinate;

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o) return true;

        // null check
        if (o == null) return false;

        // type check and cast
        if (getClass() != o.getClass()) return false;
        Centroid centroid = (Centroid) o;

        // field comparison
        return Objects.equals(xCoordinate, centroid.getXCoordinate())
                && Objects.equals(yCoordinate, centroid.getYCoordinate());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + xCoordinate.hashCode();
        result = 31 * result + yCoordinate.hashCode();

        return result;
    }
}
