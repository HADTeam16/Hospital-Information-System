package org.had.hospitalinformationsystem.OT;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.doctor.Doctor;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name="ot")
public class OT {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long otId;
    private boolean availableStatus;

    @OneToMany
    private Set<Doctor>doctors=new HashSet<Doctor>();

}