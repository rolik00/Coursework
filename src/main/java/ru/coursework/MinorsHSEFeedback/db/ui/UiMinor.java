package ru.coursework.MinorsHSEFeedback.db.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiMinor {
    private Long id;
    private Long categoryId;
    private String title;
    private float difficultyRating = 0;
    private float interestRating = 0;
    private float timeConsumptionRating = 0;
    private float totalRating = 0;
    private String selfLink;
}
