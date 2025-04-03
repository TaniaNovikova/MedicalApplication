package de.medical.app.security;

// Импортируем наш кастомный сервис для загрузки деталей пользователя.
import de.medical.app.service.CustomUserDetailService;

// Импорт аннотаций и классов Spring Framework и Spring Security.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Аннотация @Configuration указывает, что этот класс содержит определения бинов,
 * которые будут управляться контейнером Spring.
 */
@Configuration

/*
 * Аннотация @EnableWebSecurity активирует веб-безопасность в приложении и
 * обеспечивает интеграцию с Spring MVC.
 */
@EnableWebSecurity
public class SecurityConfig {

    // Сервис для загрузки деталей пользователя, реализующий UserDetailsService.
    private final CustomUserDetailService userDetailService;

    // Конструктор для внедрения зависимости CustomUserDetailService.
    public SecurityConfig(CustomUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    /*
     * Определяет бин BCryptPasswordEncoder, который используется для хэширования паролей.
     * BCrypt является надёжным алгоритмом хэширования и обеспечивает безопасность хранения паролей.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Определяет бин DaoAuthenticationProvider, который отвечает за проверку учетных данных пользователя.
     * Он использует наш CustomUserDetailService для загрузки данных пользователя из базы данных,
     * а также BCryptPasswordEncoder для проверки пароля.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Устанавливаем наш кастомный сервис, который загружает пользователя по username.
        authProvider.setUserDetailsService(userDetailService);
        // Устанавливаем механизм шифрования для проверки паролей.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /*
     * Метод filterChain настраивает цепочку фильтров безопасности для HTTP-запросов.
     * Здесь происходит конфигурация следующих аспектов:
     * 1. Отключение CSRF-защиты (для учебного проекта, а в продакшене это стоит настроить с осторожностью).
     * 2. Определение правил доступа для URL:
     *    - Все запросы, начинающиеся на /auth/** и /h2-console/**, доступны без аутентификации.
     *    - Запросы к /admin/** доступны только пользователям с ролью "ADMIN".
     *    - Все остальные запросы требуют аутентификации.
     * 3. Использование базовой HTTP-аутентификации.
     * 4. Отключение заголовка frameOptions, чтобы разрешить работу консоли H2.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключение CSRF защиты.
                .csrf(csrf -> csrf.disable())
                // Настройка авторизации HTTP-запросов.
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ без аутентификации для URL-адресов /auth/** и /h2-console/**
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                        // Доступ к URL /admin/** разрешён только для пользователей с ролью ADMIN.
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Для всех остальных запросов требуется аутентификация.
                        .anyRequest().authenticated()
                )
                // Включаем базовую HTTP-аутентификацию с настройками по умолчанию.
                .httpBasic(Customizer.withDefaults());

        // Отключаем ограничения для отображения H2-консоли во фрейме.
       // http.headers(headers -> headers.frameOptions().disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));


        // Собираем и возвращаем объект SecurityFilterChain.
        return http.build();
    }

    /*
     * Метод configure позволяет настроить AuthenticationManagerBuilder, добавив в него
     * наш кастомный AuthenticationProvider, что позволяет использовать собственную логику
     * аутентификации (через CustomUserDetailService и BCryptPasswordEncoder).
     * Этот метод может быть полезен, если потребуется дополнительная настройка менеджера аутентификации.
     */
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }
}
