package org.had.hospitalinformationsystem.nurse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.user.User;

@Entity
@Getter
@Setter
@Table(name = "nurse")
public class Nurse {
    @Id
    private Long nurseId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "userid")
    private User user;
    private boolean headNurse;
}