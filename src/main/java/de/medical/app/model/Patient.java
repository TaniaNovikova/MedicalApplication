package de.medical.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

/*
 * Класс Patient представляет пациента в медицинском приложении.
 * Он является сущностью, которая сохраняется в базе данных.
 * Lombok аннотации упрощают написание кода, генерируя необходимый шаблонный код.
 */
@Entity // Указывает, что класс является JPA-сущностью
@Data // Lombok генерирует геттеры, сеттеры, equals, hashCode и toString
@NoArgsConstructor // Создает конструктор без параметров
@AllArgsConstructor // Создает конструктор, принимающий все поля
@Builder // Позволяет создавать объекты с использованием паттерна "Строитель"
public class Patient {

    @Id // Обозначает поле как первичный ключ
    @GeneratedValue(strategy = GenerationType.AUTO) // Значение идентификатора генерируется автоматически
    private Long id; // Уникальный идентификатор пациента

    @Column(nullable = false) // Обязательное поле, не допускающее значение null
    private String name; // Имя пациента

    @Column(nullable = false) // Обязательное поле для хранения даты рождения
    private LocalDate birthDate; // Дата рождения пациента


}
