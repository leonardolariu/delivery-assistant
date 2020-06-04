package com.leonardolariu.deliveryassistant.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
@Table(	name = "drivers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @Temporal(TemporalType.DATE)
    private Calendar since;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Driver(String name, String email, User user) {
        this.name = name;
        this.email = email;
        this.user = user;
        this.since = new GregorianCalendar();
    }
}
