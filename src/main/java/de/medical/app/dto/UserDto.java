package de.medical.app.dto;

import lombok.Data;
import java.time.LocalDate;

/*
 * Класс UserDto представляет объект передачи данных (DTO) для пользователя.
 * Он используется при регистрации нового пользователя для обмена данными между клиентом и сервером.
 * С помощью Lombok-аннотации @Data автоматически генерируются геттеры, сеттеры, toString, equals и hashCode.
 */
@Data
public class UserDto {

    // Имя пользователя для входа в систему.
    private String username;

    // Пароль пользователя (в виде открытого текста; шифрование происходит при регистрации).
    private String password;

    // Имя пациента, связанного с пользователем.
    private String name;

    // Дата рождения пациента.
    private LocalDate birthDate;
}

