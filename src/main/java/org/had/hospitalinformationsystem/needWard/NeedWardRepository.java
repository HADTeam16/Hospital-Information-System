package org.had.hospitalinformationsystem.needWard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NeedWardRepository extends JpaRepository<NeedWard,Long> {

    boolean findByAppointment_AppointmentId(Long appointmentId);

    void deleteByAppointment_AppointmentId(Long appointmentId);
}
