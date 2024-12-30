package ru.coursework.MinorsHSEFeedback.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String courseTitle;
    @Column(nullable = false)
    private String minorTitle;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private int count = 0;
    @Column(nullable = false)
    private float rating = 0;
}
