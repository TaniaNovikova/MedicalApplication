package de.medical.app.repository;

import de.medical.app.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * AppointmentRepository – репозиторий для работы с сущностью Appointment.
 * Расширяя интерфейс JpaRepository, мы получаем стандартные CRUD-операции (создание, чтение, обновление, удаление)
 * без необходимости реализации методов вручную.
 *
 * Параметры:
 * - Appointment: тип сущности, с которой будем работать.
 * - Long: тип идентификатора сущности (тип поля id).
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Дополнительные методы запросов можно объявлять здесь, если понадобится специализированная логика выборки данных.
}
