package com.leonardolariu.deliveryassistant.services.utils;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Package {
    @CsvBindByName
    private double xCoordinate;
    @CsvBindByName
    private double yCoordinate;
    @CsvBindByName
    private String recipientPhoneNumber;
    @CsvBindByName
    private String additionalInfo;

    public Package(double xCoordinate, double yCoordinate, String recipientPhoneNumber, String additionalInfo) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.recipientPhoneNumber = recipientPhoneNumber.trim();
        this.additionalInfo = additionalInfo.trim();
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber.trim();
    }
}
