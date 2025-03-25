package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiReview {
    private String userName;
    private String minorTitle;
    private String body;
    private int difficultyMark;
    private int interestMark;
    private int timeConsumptionMark;
    private int totalMark;
    private LocalDateTime createDate;
}
