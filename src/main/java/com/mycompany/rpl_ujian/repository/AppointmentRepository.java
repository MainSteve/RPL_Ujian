package com.mycompany.rpl_ujian.repository;

import com.mycompany.rpl_ujian.model.Appointment;
import com.mycompany.rpl_ujian.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(User doctor);

    List<Appointment> findByPatientId(Long patientId);
}
