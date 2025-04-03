package de.medical.app.repository;

import de.medical.app.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * PatientRepository – репозиторий для работы с сущностью Patient.
 * Наследуя JpaRepository, интерфейс автоматически предоставляет стандартные CRUD-операции для работы с пациентами.
 *
 * Параметры:
 * - Patient: сущность, которую репозиторий будет обрабатывать.
 * - Long: тип идентификатора сущности (поле id).
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Здесь можно добавить дополнительные методы, если понадобятся специфичные запросы по пациентам.
}

