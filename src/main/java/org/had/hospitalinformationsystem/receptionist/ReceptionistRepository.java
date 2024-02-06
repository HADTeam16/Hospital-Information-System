package org.had.hospitalinformationsystem.receptionist;


import org.had.hospitalinformationsystem.receptionist.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist,Long> {
}
