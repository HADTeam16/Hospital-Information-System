package org.had.hospitalinformationsystem.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long doctorId;

    @OneToOne
    @MapsId
    @JoinColumn(name="user_id")
    private User user;

    private String specialization;

}
