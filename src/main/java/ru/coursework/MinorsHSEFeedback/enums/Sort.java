package ru.coursework.MinorsHSEFeedback.enums;

import lombok.Getter;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

import java.util.Comparator;

@Getter
public enum Sort {
    DIFFICULTY_SORT((m1, m2) -> Float.compare(m2.getDifficultyRating(), m1.getDifficultyRating())),
    INTEREST_SORT((m1, m2) -> Float.compare(m2.getInterestRating(), m1.getInterestRating())),
    TIME_CONSUMPTION_SORT((m1, m2) -> Float.compare(m2.getTimeConsumptionRating(), m1.getTimeConsumptionRating())),
    TOTAL_SORT((m1, m2) -> Float.compare(m2.getTotalRating(), m1.getTotalRating()));
    private final Comparator<UiMinor> comparator;
    Sort(Comparator<UiMinor> comparator) {
        this.comparator = comparator;
    }
}

