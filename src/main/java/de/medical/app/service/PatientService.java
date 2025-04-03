package de.medical.app.service;

import de.medical.app.model.Patient;
import de.medical.app.repository.*;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * PatientService инкапсулирует бизнес-логику, связанную с пациентами.
 * Он взаимодействует с PatientRepository для выполнения CRUD-операций (создание, чтение, обновление, удаление).
 *
 * Этот сервис служит промежуточным звеном между контроллерами (обрабатывающими HTTP-запросы)
 * и репозиторием, что позволяет централизованно управлять логикой работы с пациентами.
 */
@Service // Обозначает класс как сервис-компонент Spring.
@Slf4j  // Lombok-аннотация для логирования.
public class PatientService {

    private final PatientRepository patientRepository; // Репозиторий для доступа к данным пациентов.
    private final UserRepository userRepository;
    // Конструктор для внедрения зависимости PatientRepository.
    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    /*
     * Метод findAll возвращает список всех пациентов.
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /*
     * Метод findById ищет пациента по идентификатору.
     * Если пациент не найден, возвращается null.
     */
    public Patient findById(Long id) {
        log.info("Finding patient with id: {}", id);
        return patientRepository.findById(id).orElse(null);
    }

    /*
     * Метод save сохраняет или обновляет данные пациента.
     */
    public Patient save(Patient patient) {
        log.info("Saving patient: {}", patient);
        return patientRepository.save(patient);
    }

    public Long getUserIdByPatientId(Long patientId) {
        return userRepository.findUserIdByPatientId(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден для пациента с id: " + patientId));
    }

    /*
     * Метод deleteById удаляет пациента по его идентификатору.
     */
    public void deleteById(Long id) {
        log.info("Deleting patient with id: {}", id);
        patientRepository.deleteById(id);
    }
}
