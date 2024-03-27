package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.had.hospitalinformationsystem.appointment.Appointment;

import java.util.List;

@Getter
@Setter
public class PrescriptionsAndRecords {
    private List<String> records;
    private List<String> prescription;
    private Appointment appointment;

    public PrescriptionsAndRecords(List<String> records, List<String> prescription,Appointment appointment) {
        this.records = records;
        this.prescription = prescription;
        this.appointment = appointment;
    }
}
