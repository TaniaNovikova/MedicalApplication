package de.medical.app.controller;

import de.medical.app.dto.UserDto;
import de.medical.app.model.User;
import de.medical.app.service.UserService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * AuthController обрабатывает запросы, связанные с аутентификацией и регистрацией пользователей.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Сервис для работы с пользователями, включающий логику регистрации.
    private final UserService userService;

    // Конструктор для внедрения зависимости UserService.
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /*
     * Эндпоинт для регистрации нового пользователя.
     * Принимает данные в формате UserDto, вызывает метод регистрации в UserService,
     * после чего возвращает сообщение об успешной регистрации.
     */
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody UserDto dto ) {
//        User newUser = userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getName(), dto.getBirthDate());
//        return ResponseEntity.ok("User registered successfully " + newUser.getUsername());
//    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto dto) {
        // Проверяем, существует ли пользователь с таким именем.
        if (userService.existsByUsername(dto.getUsername())) {
            // Если пользователь найден, возвращаем ответ со статусом 409 Conflict.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with username " + dto.getUsername() + " already exists.");
        }

        // Если пользователя нет, выполняем регистрацию.
        User newUser = userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getName(), dto.getBirthDate());
        return ResponseEntity.ok("User registered successfully: " + newUser.getUsername());
    }

}
