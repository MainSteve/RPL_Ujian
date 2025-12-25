package com.mycompany.rpl_ujian.service;

import com.mycompany.rpl_ujian.model.Appointment;
import com.mycompany.rpl_ujian.model.Prescription;
import com.mycompany.rpl_ujian.repository.AppointmentRepository;
import com.mycompany.rpl_ujian.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Transactional
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public void addPrescription(Prescription prescription) {
        prescriptionRepository.save(prescription);
    }

    @Transactional
    public void rescheduleAppointment(Long id, java.util.Date newDate) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isPresent()) {
            Appointment a = opt.get();
            a.setAppointmentDate(newDate);
            a.setStatus("RESCHEDULED");
            appointmentRepository.save(a);
        }
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isPresent()) {
            Appointment a = opt.get();
            a.setStatus("CANCELLED");
            appointmentRepository.save(a);
        }
    }

    public List<Prescription> getPrescriptionsByAppointment(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId);
    }
}
