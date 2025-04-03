package de.medical.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * Класс Appointment представляет запись на прием или назначение встречи.
 * Это сущность, которая сохраняется в базе данных и связывается с пациентом.
 */
@Entity // Обозначает класс как JPA-сущность
@Data // Lombok генерирует стандартные методы (геттеры, сеттеры, toString, equals, hashCode)
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор, принимающий все поля
@Builder // Реализует паттерн "Строитель" для удобного создания объектов
public class Appointment {

    @Id // Помечает поле как первичный ключ
    @GeneratedValue(strategy = GenerationType.AUTO) // Автоматическая генерация идентификатора
    private Long id; // Уникальный идентификатор записи

    @Column(nullable = false) // Обязательное поле для заполнения
    private LocalDateTime dateTime; // Дата и время проведения приема

    @ManyToOne(fetch = FetchType.EAGER) // Определяет связь "многие-к-одному" с сущностью Patient; данные пациента загружаются сразу
    @JoinColumn(name = "patient_id") // Указывает имя столбца, используемого в качестве внешнего ключа для связи с Patient
    private Patient patient; // Пациент, к которому относится данная запись
}
