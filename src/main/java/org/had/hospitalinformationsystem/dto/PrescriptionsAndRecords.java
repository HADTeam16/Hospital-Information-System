package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrescriptionsAndRecords {
    private List<String> records;
    private List<String> prescription;

    public PrescriptionsAndRecords(List<String> records, List<String> prescription) {
        this.records = records;
        this.prescription = prescription;
    }
}
