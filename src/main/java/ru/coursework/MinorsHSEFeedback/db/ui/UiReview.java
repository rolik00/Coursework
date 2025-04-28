package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiReview {
    private Long id;
    private String userName;
    private String courseTitle;
    private String minorTitle;
    private String body;
    private int difficultyMark;
    private int interestMark;
    private int timeConsumptionMark;
    private int totalMark;
    private LocalDate createDate;
    private int commentsCount;
    private int likesCount;
    private int dislikesCount;
    private float value;
}
