package org.had.hospitalinformationsystem.receptionist;



import org.had.hospitalinformationsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist,Long>{

    public Receptionist findByUser(User user);
}
