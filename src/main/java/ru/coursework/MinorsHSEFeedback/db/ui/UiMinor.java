package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UiMinor {
    private Long id;
    private Long categoryTitle;
    private String title;
    private float rating = 0;
    private float difficultyRating = 0;
    private float interestRating = 0;
    private float timeConsumptionRating = 0;
}
