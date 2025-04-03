package de.medical.app.utils;

import de.medical.app.model.*;
import de.medical.app.repository.*;
import jakarta.annotation.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.stereotype.*;

import java.time.*;



    @Component
    public class DataInit {

        private final UserRepository userRepository;
        private final PatientRepository patientRepository;
        private final AppointmentRepository appointmentRepository; // Новое поле
        private final BCryptPasswordEncoder passwordEncoder;

        // Изменённый конструктор с добавлением AppointmentRepository
        public DataInit(UserRepository userRepository, PatientRepository patientRepository,
                        AppointmentRepository appointmentRepository, BCryptPasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.patientRepository = patientRepository;
            this.appointmentRepository = appointmentRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @PostConstruct
        public void init() {
            if(userRepository.findByUsername("admin") == null){
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }

            if(userRepository.findByUsername("user1") == null){
                // Создание пациента для user1
                Patient patient = new Patient();
                patient.setName("Max Mustermann");
                patient.setBirthDate(LocalDate.of(1990, 1, 1));
                patientRepository.save(patient);

                // Создание первой записи для пациента
                Appointment appointment1 = new Appointment();
                appointment1.setDateTime(LocalDateTime.of(2025, 4, 1, 15, 0));
                appointment1.setPatient(patient);
                appointmentRepository.save(appointment1);

                // Создание второй записи для пациента
                Appointment appointment2 = new Appointment();
                appointment2.setDateTime(LocalDateTime.of(2025, 4, 2, 16, 0));
                appointment2.setPatient(patient);
                appointmentRepository.save(appointment2);

                // Создание пользователя, связанного с пациентом
                User user = new User();
                user.setUsername("user1");
                user.setPassword(passwordEncoder.encode("user111"));
                user.setRole("ROLE_USER");
                user.setPatient(patient);
                userRepository.save(user);
            }

            if(userRepository.findByUsername("user2") == null){
                Patient patient = new Patient();
                patient.setName("Erika Mustermann");
                patient.setBirthDate(LocalDate.of(1992, 2, 2));
                patientRepository.save(patient);

                Appointment appointment = new Appointment();
                appointment.setDateTime(LocalDateTime.of(2025, 4, 3, 10, 0));
                appointment.setPatient(patient);
                appointmentRepository.save(appointment);

                User user = new User();
                user.setUsername("user2");
                user.setPassword(passwordEncoder.encode("user222"));
                user.setRole("ROLE_USER");
                user.setPatient(patient);
                userRepository.save(user);
            }

        }
    }


