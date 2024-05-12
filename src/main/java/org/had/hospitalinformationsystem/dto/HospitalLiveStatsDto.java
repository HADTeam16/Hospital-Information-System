package org.had.hospitalinformationsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HospitalLiveStatsDto {
    private Long totalPatientsCount;
    private Long currentlyScheduledAppointmentCount;
    private Map<String, Long> specialityWiseDoctorsCount;
    private int otsAvailable;
    private int totalOts;
    private int totalWards;
    private int availableWards;
    private int totalAttendedAppointments;
    private int totalAttendedPatients;
    private int wardsAssignedTillDate;
    private int currentlyAssignedPatientsCount;
    private int totalWardsAllottedCount;
    private Map<String, Long> currentlyAvailableSpecialityWiseDoctorsCount;

}
