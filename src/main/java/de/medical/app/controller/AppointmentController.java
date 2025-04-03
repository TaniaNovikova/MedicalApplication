package de.medical.app.controller;

import de.medical.app.model.*;
import de.medical.app.repository.*;
import de.medical.app.service.*;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/*
 * AppointmentController обрабатывает HTTP-запросы, связанные с записями на приём.
 * Он предоставляет эндпоинты для получения и создания записей (appointments).
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    // Репозиторий для доступа к данным записей.
    private final AppointmentRepository appointmentRepository;

    // Сервис для работы с пользователями, необходим для получения текущего аутентифицированного пользователя.
    private final UserService userService;
    private final PatientRepository patientRepository;

    // Конструктор для внедрения зависимостей.
    public AppointmentController(AppointmentRepository appointmentRepository, UserService userService, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.patientRepository = patientRepository;

    }

    /*
     * Эндпоинт для получения записей.
     * Если текущий пользователь имеет роль ADMIN, возвращаются все записи.
     * В противном случае возвращаются только записи, связанные с пациентом данного пользователя.
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        User currentUser = getCurrentUser();
        if ("ROLE_ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.ok(appointmentRepository.findAll());
        } else {
            Long patientId = currentUser.getPatient().getId();
            List<Appointment> appointments = appointmentRepository.findAll().stream()
                    .filter(appointment -> appointment.getPatient().getId().equals(patientId))
                    .toList();
            return ResponseEntity.ok(appointments);
        }
    }

//    *//*
//     * Эндпоинт для создания новой записи.
//     * При создании назначается пациент, связанный с текущим пользователем.
//     * После сохранения возвращается сообщение об успешном создании записи.
//     *//*

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment request) {
        // Проверяем, что в запросе указан пациент (через вложенный объект с заполненным id)
        if (request.getPatient() == null || request.getPatient().getId() == null) {
            return ResponseEntity.badRequest().body("Patient ID must be provided");
        }

        // Загружаем пациента из базы данных по id
        Optional<Patient> optionalPatient = patientRepository.findById(request.getPatient().getId());
        if (optionalPatient.isEmpty()) {
            return ResponseEntity.badRequest().body("Patient not found");
        }
        Patient patient = optionalPatient.get();

        // Получаем текущего аутентифицированного пользователя
        User currentUser = getCurrentUser();

        // Если текущий пользователь не является администратором,
        // то проверяем, что он создаёт запись только для себя (т.е. id пациента из запроса совпадает с id его связанного пациента)
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            if (currentUser.getPatient() == null || !currentUser.getPatient().getId().equals(patient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to create appointment for another patient");
            }
        }

        // Устанавливаем корректного пациента для записи
        request.setPatient(patient);

        // Сохраняем запись
        appointmentRepository.save(request);
        return ResponseEntity.ok("Appointment created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable("id") Long id) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);
        if (appointmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Appointment appointment = appointmentOptional.get();

        User currentUser = getCurrentUser();

        // Если пользователь не администратор, проверяем, что он пытается удалить свою запись
        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            if (currentUser.getPatient() == null ||
                    !currentUser.getPatient().getId().equals(appointment.getPatient().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to delete this appointment");
            }
        }

        appointmentRepository.deleteById(id);
        return ResponseEntity.ok("Appointment deleted successfully");
    }



    /*
     * Метод для получения текущего аутентифицированного пользователя.
     * Извлекает имя пользователя из SecurityContext и ищет пользователя в базе через UserService.
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByUsername(username);
    }
}
