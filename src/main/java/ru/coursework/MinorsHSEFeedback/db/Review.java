package ru.coursework.MinorsHSEFeedback.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long minorId;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private int difficultyMark = 0;
    @Column(nullable = false)
    private int interestMark = 0;
    @Column(nullable = false)
    private int timeConsumptionMark = 0;
    @Column(nullable = false)
    private int totalMark = 0;
    @Column(nullable = false)
    private float value = 0;
    @Column(nullable = false)
    private LocalDateTime createDate;
}