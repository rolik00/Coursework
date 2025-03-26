package ru.coursework.MinorsHSEFeedback.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewPatch {
    private String body;
    private Integer difficultyMark;
    private Integer interestMark;
    private Integer timeConsumptionMark;
    private Integer totalMark;
}
