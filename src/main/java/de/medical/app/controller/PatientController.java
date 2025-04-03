package de.medical.app.controller;

import de.medical.app.model.*;
import de.medical.app.repository.*;
import de.medical.app.service.PatientService;
import de.medical.app.service.UserService;
import jakarta.persistence.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/*
 * PatientController обрабатывает HTTP-запросы, связанные с данными пациентов.
 * Он предоставляет эндпоинты для получения списка пациентов, поиска по идентификатору и удаления данных пациента.
 */
@RestController
@RequestMapping("/patients")
@Slf4j  // Lombok-аннотация для логирования.

public class PatientController {

    // Сервис для операций с пациентами (CRUD-операции).
    private final PatientService patientService;

    // Сервис для работы с пользователями (необходим для получения текущего аутентифицированного пользователя).
    private final UserService userService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;


    // Конструктор для внедрения зависимостей.
    public PatientController(PatientService patientService, UserService userService, UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.patientService = patientService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /*
     * Эндпоинт для получения данных пациентов.
     * Администраторы могут получить список всех пациентов,
     * а обычные пользователи — только данные, связанные с их учётной записью.
     */
    @GetMapping
    public ResponseEntity<?> getAll(){
        User currentUser = getCurrentUser();
        if("ROLE_ADMIN".equals(currentUser.getRole())){
            return ResponseEntity.ok(patientService.findAll());
        } else {
            return ResponseEntity.ok(currentUser.getPatient());
        }
    }

    /*
     * Эндпоинт для получения данных пациента по идентификатору.
     * Администраторы получают данные пациента напрямую,
     * а для обычных пользователей проверяется, совпадает ли запрашиваемый пациент с их связанным пациентом.
     */
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getById(Long id){
//        User currentUser = getCurrentUser();
//        Patient patient = patientService.findById(id);
//        if(patient == null){
//            return ResponseEntity.notFound().build();
//        }
//        if("ROLE_ADMIN".equals(currentUser.getRole())){
//            return ResponseEntity.ok(patient);
//        } else {
//            // Если пациент не совпадает с текущим пользователем, возвращается false.
//            return ResponseEntity.ok(patient.equals(currentUser.getPatient()));
//        }
//    }


@GetMapping("/{id}")
public ResponseEntity<?> getById(@PathVariable("id") Long id) {
    User currentUser = getCurrentUser();
    Patient patient = patientService.findById(id);

    if (patient == null) {
        return ResponseEntity.notFound().build();
    }

    // Если текущий пользователь – администратор, разрешаем доступ
    if ("ROLE_ADMIN".equals(currentUser.getRole())) {
        return ResponseEntity.ok(patient);
    }

    // Если текущий пользователь является тем же пациентом, что запрашиваемый
    if (patient.equals(currentUser.getPatient())) {
        return ResponseEntity.ok(patient);
    }

    // Иначе доступ запрещён
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}



    /*
     * Эндпоинт для удаления данных юзера по идентификатору.
     * Удаление разрешено только администраторам.
     */
//
//
    @DeleteMapping
    public ResponseEntity<?> deleteByUserId(Long id){
        User currentUser = getCurrentUser();

        if("ROLE_ADMIN".equals(currentUser.getRole())){
            Optional<User> user = userRepository.findById(id);
            if(!user.isEmpty()){
                userService.deleteUser(user.get());

                List<Appointment> appointments = appointmentRepository.findAll().stream()
                        .filter(appointment -> appointment.getPatient().getId().equals(user.get().getPatient().getId()))
                        .toList();
                appointmentRepository.deleteAll(appointments);

                patientService.deleteById(user.get().getPatient().getId());


                return ResponseEntity.ok().build();
            }
            else {
                return ResponseEntity.notFound().build();
            }

        }
        else {
            return ResponseEntity.status(403).body("You are not allowed to delete this patient");
        }
    }

    /*
     * Эндпоинт для удаления данных пациента по идентификатору.
     * Удаление разрешено только администраторам.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatientById(@PathVariable("id") Long id) {
        // Получаем текущего пользователя (например, из security context)
        User currentUser = getCurrentUser();

        if ("ROLE_ADMIN".equals(currentUser.getRole())) {
            // По id пациента получаем id пользователя, связанного с данным пациентом
            Long userId;
            try {
                userId = patientService.getUserIdByPatientId(id);
            } catch (EntityNotFoundException ex) {
                return ResponseEntity.notFound().build();
            }

            // Ищем пользователя по полученному id
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Удаляем пользователя
                userService.deleteUser(user);

                // Находим и удаляем все встречи, связанные с данным пациентом
                List<Appointment> appointments = appointmentRepository.findAll().stream()
                        .filter(appointment -> appointment.getPatient().getId().equals(id))
                        .toList();
                appointmentRepository.deleteAll(appointments);

                // Удаляем пациента
                patientService.deleteById(id);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(403).body("You are not allowed to delete this patient");
        }
    }



    /*
     * Метод для получения текущего аутентифицированного пользователя.
     * Извлекает информацию из SecurityContext и через UserService находит пользователя по имени.
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByUsername(username);
    }
}
