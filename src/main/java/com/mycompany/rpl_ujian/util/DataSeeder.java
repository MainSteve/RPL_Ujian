package com.mycompany.rpl_ujian.util;

import com.mycompany.rpl_ujian.model.Appointment;
import com.mycompany.rpl_ujian.model.Patient;
import com.mycompany.rpl_ujian.model.User;
import com.mycompany.rpl_ujian.repository.AppointmentRepository;
import com.mycompany.rpl_ujian.repository.PatientRepository;
import com.mycompany.rpl_ujian.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private com.mycompany.rpl_ujian.repository.DoctorScheduleRepository doctorScheduleRepository;

    @Transactional
    public void seed() {
        System.out.println("Starting Data Seeding...");
        seedUsers();
        seedPatients();
        seedAppointments();
        seedSchedules();
        System.out.println("Data Seeding Completed.");
    }

    private void seedUsers() {
        // Seed Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", hashPassword("admin123"), "ADMIN", "Administrator");
            userRepository.save(admin);
            System.out.println("Seeded Admin user.");
        }

        // Seed Doctors
        if (userRepository.findByUsername("doctor1").isEmpty()) {
            User doctor1 = new User("doctor1", hashPassword("doc123"), "DOCTOR", "Dr. Strange");
            userRepository.save(doctor1);
            System.out.println("Seeded Doctor1.");
        }

        if (userRepository.findByUsername("doctor2").isEmpty()) {
            User doctor2 = new User("doctor2", hashPassword("doc123"), "DOCTOR", "Dr. House");
            userRepository.save(doctor2);
            System.out.println("Seeded Doctor2.");
        }

        // Seed Patient Users
        if (userRepository.findByUsername("patient1").isEmpty()) {
            User patientUser = new User("patient1", hashPassword("pat123"), "PATIENT", "John Doe User");
            userRepository.save(patientUser);
            System.out.println("Seeded Patient user 1.");
        }

        if (userRepository.findByUsername("patient2").isEmpty()) {
            User patientUser2 = new User("patient2", hashPassword("pat123"), "PATIENT", "Jane Smith User");
            userRepository.save(patientUser2);
            System.out.println("Seeded Patient user 2.");
        }
    }

    private void seedPatients() {
        // Seed or Update John Doe
        Optional<Patient> p1Opt = patientRepository.findByNik("1234567890");
        if (p1Opt.isEmpty()) {
            Patient p1 = new Patient("John Doe", "1234567890", "123 Main St", "08123456789", new Date());
            userRepository.findByUsername("patient1").ifPresent(p1::setUser);
            patientRepository.save(p1);
            System.out.println("Seeded Patient John Doe.");
        } else {
            // Repair link if missing
            Patient p1 = p1Opt.get();
            if (p1.getUser() == null) {
                userRepository.findByUsername("patient1").ifPresent(u -> {
                    p1.setUser(u);
                    patientRepository.save(p1);
                    System.out.println("Linked User to existing Patient John Doe.");
                });
            }
        }

        // Seed or Update Jane Smith
        Optional<Patient> p2Opt = patientRepository.findByNik("0987654321");
        if (p2Opt.isEmpty()) {
            Patient p2 = new Patient("Jane Smith", "0987654321", "456 Oak Ave", "08987654321", new Date());
            userRepository.findByUsername("patient2").ifPresent(p2::setUser);
            patientRepository.save(p2);
            System.out.println("Seeded Patient Jane Smith.");
        } else {
            // Repair link if missing
            Patient p2 = p2Opt.get();
            if (p2.getUser() == null) {
                userRepository.findByUsername("patient2").ifPresent(u -> {
                    p2.setUser(u);
                    patientRepository.save(p2);
                    System.out.println("Linked User to existing Patient Jane Smith.");
                });
            }
        }
    }

    private void seedAppointments() {
        if (appointmentRepository.count() == 0) {
            List<User> doctors = userRepository.findByRole("DOCTOR");
            List<Patient> patients = patientRepository.findAll();

            if (!doctors.isEmpty() && !patients.isEmpty()) {
                Appointment a1 = new Appointment(patients.get(0), doctors.get(0), new Date(), "SCHEDULED");
                a1.setNotes("Regular checkup");
                appointmentRepository.save(a1);

                Appointment a2 = new Appointment(patients.get(1), doctors.get(1), new Date(), "SCHEDULED");
                a2.setNotes("Headache complaint");
                appointmentRepository.save(a2);

                System.out.println("Appointments seeded.");
            } else {
                System.out.println("Skipping appointment seeding: Doctors or Patients missing.");
            }
        }
    }

    private void seedSchedules() {
        if (doctorScheduleRepository.count() == 0) {
            userRepository.findByUsername("doctor1").ifPresent(doc -> {
                com.mycompany.rpl_ujian.model.DoctorSchedule s1 = new com.mycompany.rpl_ujian.model.DoctorSchedule(
                        doc, java.time.DayOfWeek.MONDAY, java.time.LocalTime.of(9, 0), java.time.LocalTime.of(12, 0));
                com.mycompany.rpl_ujian.model.DoctorSchedule s2 = new com.mycompany.rpl_ujian.model.DoctorSchedule(
                        doc, java.time.DayOfWeek.WEDNESDAY, java.time.LocalTime.of(13, 0),
                        java.time.LocalTime.of(16, 0));
                doctorScheduleRepository.save(s1);
                doctorScheduleRepository.save(s2);
                System.out.println("Seeded Schedules for Doctor1.");
            });
        }
    }

    private String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }
}
