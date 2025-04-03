package de.medical.app.service;

import de.medical.app.model.Patient;
import de.medical.app.model.User;
import de.medical.app.repository.PatientRepository;
import de.medical.app.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/*
 * UserService отвечает за бизнес-логику, связанную с пользователями.
 * Он занимается регистрацией новых пользователей, шифрованием паролей и поиском пользователей по имени.
 *
 * При регистрации нового пользователя создается также объект пациента.
 * Шифрование пароля с использованием BCryptPasswordEncoder обеспечивает безопасность данных.
 */
@Service // Обозначает класс как компонент сервисного уровня Spring.
public class UserService {

    private final UserRepository userRepository;         // Репозиторий для работы с данными пользователей.
    private final PatientRepository patientRepository;       // Репозиторий для работы с данными пациентов.
    private final BCryptPasswordEncoder passwordEncoder;     // Компонент для шифрования паролей.

    // Конструктор для внедрения зависимостей.
    public UserService(UserRepository userRepository, PatientRepository patientRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Метод registerUser выполняет регистрацию нового пользователя.
     * Он создает объект пациента, сохраняет его, затем создает объект пользователя,
     * шифрует пароль и привязывает пациента к пользователю перед сохранением.
     */
    public User registerUser(String username, String rawPassword, String name, LocalDate birthDate) {
        // Создаем объект пациента и устанавливаем его данные.
        Patient patient = new Patient();
        patient.setName(name);
        patient.setBirthDate(birthDate);
        // Сохраняем пациента в базе данных.
        patientRepository.save(patient);

        // Создаем объект пользователя.
        User user = new User();
        user.setUsername(username);
        // Шифруем пароль для безопасного хранения.
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER"); // Устанавливаем роль пользователя.
        user.setPatient(patient);  // Привязываем созданного пациента к пользователю.
        // Сохраняем пользователя в базе данных.
        return userRepository.save(user);
    }

    /*
     * Метод findByUsername возвращает пользователя по его имени.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    /**
     * Проверяет, существует ли пользователь с заданным именем.
     *
     * @param username имя пользователя для проверки
     * @return true, если пользователь существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username)!=null;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}
