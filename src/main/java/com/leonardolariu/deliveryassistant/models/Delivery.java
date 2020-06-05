package com.leonardolariu.deliveryassistant.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Table(	name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Calendar date;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EDeliveryStatus status;

    private int estimatedDriversCount;

    private int actualDriversCount;

    private int packagesCount;

    private double minimumDistanceToCover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Delivery(EDeliveryStatus deliveryStatus, User user) {
        this.date = new GregorianCalendar();
        this.status = deliveryStatus;
        this.user = user;
    }



    public String getDeliveryDateString() {
        return (new SimpleDateFormat("dd/MM/yyyy")).format(date.getTime());
    }
}
