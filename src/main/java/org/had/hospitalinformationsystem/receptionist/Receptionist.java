package org.had.hospitalinformationsystem.receptionist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.user.User;

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
