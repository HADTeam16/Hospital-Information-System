package org.had.hospitalinformationsystem.records;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;

@Entity
@Getter
@Setter
@Table(name = "records")
public class Records {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long recordsId;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String recordImage;


}
