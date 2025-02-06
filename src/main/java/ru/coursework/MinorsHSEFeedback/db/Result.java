package ru.coursework.MinorsHSEFeedback.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long minorId;
    @Column(nullable = false)
    private int reviewsCount = 0;
    @Column(nullable = false)
    private int difficultyMarkSum = 0;
    @Column(nullable = false)
    private int interestMarkSum = 0;
    @Column(nullable = false)
    private int timeConsumptionMarkSum = 0;
    @Column(nullable = false)
    private int totalMarkSum = 0;
}

