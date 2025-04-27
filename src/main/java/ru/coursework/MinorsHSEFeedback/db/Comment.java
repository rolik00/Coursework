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

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "text")
    private String body;
    @Column(nullable = false)
    private Long reviewId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDate createDate;
    @Column
    private Long parentId;
}
