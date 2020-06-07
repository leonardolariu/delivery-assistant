package com.leonardolariu.deliveryassistant.services.utils;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Package {
    @CsvBindByName
    private int order;
    @CsvBindByName
    private Double xCoordinate;
    @CsvBindByName
    private Double yCoordinate;
    @CsvBindByName
    private String recipientPhoneNumber;
    @CsvBindByName
    private String additionalInfo;

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber.trim();
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo.trim();
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o) return true;

        // null check
        if (o == null) return false;

        // type check and cast
        if (getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;

        // field comparison
        return Objects.equals(xCoordinate, aPackage.getXCoordinate())
                && Objects.equals(yCoordinate, aPackage.getYCoordinate())
                && Objects.equals(recipientPhoneNumber, aPackage.getRecipientPhoneNumber())
                && Objects.equals(additionalInfo, aPackage.getAdditionalInfo());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + xCoordinate.hashCode();
        result = 31 * result + yCoordinate.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return order + "," + xCoordinate + "," + yCoordinate + "," + recipientPhoneNumber + "," + additionalInfo + "\n";
    }
}
