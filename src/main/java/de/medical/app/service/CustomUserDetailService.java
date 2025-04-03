package de.medical.app.service;

import de.medical.app.model.User;
import de.medical.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/*
 * CustomUserDetailService реализует интерфейс UserDetailsService из Spring Security.
 * Его основная задача — загрузить детали пользователя по имени для проведения аутентификации.
 *
 * Когда пользователь пытается войти в систему, Spring Security вызывает метод loadUserByUsername.
 * В этом методе осуществляется поиск пользователя в базе данных через UserRepository.
 * Если пользователь не найден, генерируется исключение UsernameNotFoundException.
 * Если пользователь найден, возвращается объект, реализующий UserDetails,
 * содержащий имя, зашифрованный пароль и список прав (ролей).
 *
 * Это необходимо для того, чтобы Spring Security мог корректно проверить данные аутентификации
 * и определить, к каким ресурсам пользователь имеет доступ.
 */
@Service // Обозначает класс как компонент сервисного уровня Spring.
@Slf4j  // Lombok-аннотация для удобного логирования.
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository; // Репозиторий для доступа к данным пользователей.

    // Конструктор для внедрения зависимости UserRepository.
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Метод loadUserByUsername используется Spring Security для загрузки информации о пользователе.
     * Если пользователь не найден, выбрасывается UsernameNotFoundException.
     * Если найден, возвращается объект, содержащий имя, пароль и назначенные роли.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Поиск пользователя по имени (username) в базе данных.
        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            log.info("User not found: {}", username);
            throw new UsernameNotFoundException("User not found" + username);
        }

        // Формирование объекта UserDetails с назначением одной роли из userEntity.
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole()))
        );
    }
}
