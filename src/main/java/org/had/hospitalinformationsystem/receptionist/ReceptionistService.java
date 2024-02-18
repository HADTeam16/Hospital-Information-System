package org.had.hospitalinformationsystem.receptionist;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReceptionistService {
    private final SimpMessagingTemplate messagingTemplate;

    public ReceptionistService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendAppointmentUpdate(Appointment appointment) {
        messagingTemplate.convertAndSend("/topic/appointments", appointment);
    }
}
