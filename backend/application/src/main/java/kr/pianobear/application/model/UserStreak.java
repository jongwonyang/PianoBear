package kr.pianobear.application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class UserStreak {

    @Id
    private String userId;

    private int currentStreak;
    private int maxStreak;
    private LocalDate lastPracticedDate;
}
