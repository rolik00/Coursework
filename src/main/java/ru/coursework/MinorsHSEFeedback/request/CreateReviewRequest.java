package ru.coursework.MinorsHSEFeedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    @NotNull
    private String minorTitle;
    @NotNull
    private String email;
    @NotNull
    private String body;
    @NotNull
    private int difficultyMark;
    @NotNull
    private int interestMark;
    @NotNull
    private int timeConsumptionMark;
    @NotNull
    private int totalMark = 0;
}
