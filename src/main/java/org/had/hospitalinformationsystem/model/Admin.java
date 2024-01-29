package org.had.hospitalinformationsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="admin")
public class Admin {
    @Id
    private Long id;
    @Column(unique = true)
    private String userName;
    private String password;
}
