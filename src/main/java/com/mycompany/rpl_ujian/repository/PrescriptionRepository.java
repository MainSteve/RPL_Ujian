package com.mycompany.rpl_ujian.repository;

import com.mycompany.rpl_ujian.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByAppointmentId(Long appointmentId);
}
