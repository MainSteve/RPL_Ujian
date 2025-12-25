package com.mycompany.rpl_ujian.repository;

import com.mycompany.rpl_ujian.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByNik(String nik);

    Optional<Patient> findByUser(com.mycompany.rpl_ujian.model.User user);

    List<Patient> findByNameContainingIgnoreCase(String name);
}
