package de.medical.app.model;

// Импортируем необходимые аннотации для работы с JPA и Lombok
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Класс User представляет пользователя системы.
 * Он является сущностью JPA, что позволяет сохранять данные в базе данных.
 * С помощью Lombok аннотаций генерируются конструкторы, геттеры, сеттеры и методы toString/equals/hashCode.
 */
@Entity // Указывает, что данный класс является сущностью для JPA
@Table(name = "users") // Определяет таблицу "users" в базе данных для хранения пользователей
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор, принимающий все поля в качестве параметров
@Data // Автоматически генерирует геттеры, сеттеры, toString, equals и hashCode методы
@Builder // Реализует паттерн "Строитель" для удобного создания объектов
public class User {

    @Id // Помечает поле как первичный ключ сущности
    @GeneratedValue(strategy = GenerationType.AUTO) // Автоматически генерирует значение идентификатора
    private Long id; // Уникальный идентификатор пользователя

    @Column(unique = true, nullable = false) // Определяет столбец как уникальный и обязательный для заполнения
    private String username; // Имя пользователя (логин)

    @Column(nullable = false) // Поле обязательно для заполнения
    private String password; // Пароль пользователя

    @Column(nullable = false) // Поле обязательно для заполнения
    private String role; // Роль пользователя (например, "ADMIN", "USER")

    @OneToOne(fetch = FetchType.EAGER) // Устанавливает связь один-к-одному с сущностью Patient; данные пациента загружаются вместе с пользователем
    @JoinColumn(name = "patient_id") // Указывает имя столбца внешнего ключа, связывающего с таблицей пациентов
    private Patient patient; // Связанный пациент, если пользователь представляет пациента



}
