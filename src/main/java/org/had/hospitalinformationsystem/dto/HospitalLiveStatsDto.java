package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HospitalLiveStatsDto {
    // General data
    private Long totalPatientsCount;
    private Long currentlyScheduledAppointmentCount;
    private Map<String, Long> specialityWiseDoctorsCount; // Key: Speciality, Value: Count
    private int otsAvailable;
    private int totalOts;
    private int totalWards;
    private int availableWards;

    // Logged in doctor data
    private int totalAttendedAppointments;
    private int totalAttendedPatients;
    private int wardsAssignedTillDate;

    // Logged in nurse data
    private int currentlyAssignedPatientsCount;
    private int totalWardsAllottedCount;

    // Logged in receptionist data
    private Map<String, Long> currentlyAvailableSpecialityWiseDoctorsCount;

}
