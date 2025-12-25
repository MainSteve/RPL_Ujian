package com.mycompany.rpl_ujian.service;

import com.mycompany.rpl_ujian.model.Patient;
import com.mycompany.rpl_ujian.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByNik(String nik) {
        return patientRepository.findByNik(nik);
    }

    public Optional<Patient> getPatientByUser(com.mycompany.rpl_ujian.model.User user) {
        return patientRepository.findByUser(user);
    }

    @Transactional
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
