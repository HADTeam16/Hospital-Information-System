package org.had.hospitalinformationsystem.repository;


import org.had.hospitalinformationsystem.model.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist,Long> {
}
