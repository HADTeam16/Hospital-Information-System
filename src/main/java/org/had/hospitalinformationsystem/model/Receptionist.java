package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name="receptionist")
public class Receptionist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long receptionistId;

    @OneToOne
    @MapsId
    @JoinColumn(name="user_id")
    private User user;

}
