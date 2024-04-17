package org.had.hospitalinformationsystem.wardHistory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.ward.Ward;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class wardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wardHistoryId;
    private LocalDateTime statlog;
    private String notes;

    @ManyToOne
    @JoinColumn(name="appointment_id")
    private Ward ward;
}
