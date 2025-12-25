package com.mycompany.rpl_ujian.service;

import com.mycompany.rpl_ujian.model.DoctorSchedule;
import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DoctorScheduleService {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    public List<DoctorSchedule> getSchedulesByDoctor(User doctor) {
        return doctorScheduleRepository.findByDoctor(doctor);
    }

    @Transactional
    public DoctorSchedule saveSchedule(DoctorSchedule schedule) {
        return doctorScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        doctorScheduleRepository.deleteById(id);
    }
}
