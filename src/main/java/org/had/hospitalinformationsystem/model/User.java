package org.had.hospitalinformationsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="user")
@Entity
public class User {
    @Id
    private Long userId;
    private String userName;
    private String password;
    private String email;


}
