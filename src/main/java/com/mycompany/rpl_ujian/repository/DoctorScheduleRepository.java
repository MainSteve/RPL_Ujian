package com.mycompany.rpl_ujian.repository;

import com.mycompany.rpl_ujian.model.DoctorSchedule;
import com.mycompany.rpl_ujian.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctor(User doctor);
}
