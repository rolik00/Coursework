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
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long minorId;
    @Column(nullable = false, columnDefinition = "text")
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
    private LocalDate createDate;
}