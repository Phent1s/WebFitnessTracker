package com.project.webfitnesstracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workouts_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(nullable = false)
    @NotBlank(message = "Workout type required!")
    private String type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "duration_minutes", nullable = false)
    @NotNull
    private Integer durationInMinutes;

    @Column(name = "calories_burned", nullable = false)
    @NotNull
    private Integer caloriesBurned;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Workout workout = (Workout) o;
        return getId() != null && Objects.equals(getId(), workout.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Workout{" +
               "id=" + id +
               ", owner=" + owner +
               ", type=" + type +
               ", date=" + date +
               ", durationInMinutes=" + durationInMinutes +
               ", caloriesBurned=" + caloriesBurned +
               '}';
    }
}
