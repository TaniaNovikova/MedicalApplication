package de.medical.app.repository;

import de.medical.app.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

/*
 * UserRepository – репозиторий для работы с сущностью User.
 * Помимо стандартных CRUD-операций, благодаря Spring Data JPA можно определять методы,
 * которые будут автоматически реализованы на основании соглашений об именовании.
 *
 * Параметры:
 * - User: сущность, с которой будем работать.
 * - Long: тип идентификатора сущности (поле id).
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Метод для поиска пользователя по имени (username).
    // Spring Data JPA автоматически формирует SQL-запрос на основе имени метода.
    User findByUsername(String username);


    /**
     * Метод для получения идентификатора пользователя по идентификатору пациента.
     *
     * @param patientId идентификатор пациента, по которому производится поиск
     * @return Optional, содержащий идентификатор пользователя, если он найден;
     * в противном случае возвращается пустой Optional
     */
    @Query("SELECT u.id FROM User u WHERE u.patient.id = :patientId") // JPQL-запрос, выбирающий идентификатор пользователя по идентификатору пациента
    Optional<Long> findUserIdByPatientId(@Param("patientId") Long patientId); // Аннотация @Param связывает параметр метода с параметром запроса



}
