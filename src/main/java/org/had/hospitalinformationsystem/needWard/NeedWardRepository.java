package org.had.hospitalinformationsystem.needWard;

import org.had.hospitalinformationsystem.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NeedWardRepository extends JpaRepository<NeedWard,Long> {

    boolean findByAppointment_AppointmentId(Long appointmentId);

    void deleteByAppointment_AppointmentId(Long appointmentId);

    @Query("Select n FROM NeedWard n")
    List<NeedWard> returnNeedWards();


//    void delete(Optional<NeedWard> needWard);
}
