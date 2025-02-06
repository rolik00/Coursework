package ru.coursework.MinorsHSEFeedback.enums;

import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

import java.util.Comparator;

public enum Sort {
    DIFFICULTY_SORT(new Comparator<UiMinor>() {
        public int compare(UiMinor m1, UiMinor m2) {
            return Float.compare(m2.getDifficultyRating(), m1.getDifficultyRating());
        }
    }),
    INTEREST_SORT(new Comparator<UiMinor>() {
        public int compare(UiMinor m1, UiMinor m2) {
            return Float.compare(m2.getInterestRating(), m1.getInterestRating());
        }
    }),
    TIME_CONSUMPTION_SORT(new Comparator<UiMinor>() {
        public int compare(UiMinor m1, UiMinor m2) {
            return Float.compare(m2.getTimeConsumptionRating(), m1.getTimeConsumptionRating());
        }
    }),
    TOTAL_SORT(new Comparator<UiMinor>() {
        public int compare(UiMinor m1, UiMinor m2) {
            return Float.compare(m2.getTotalRating(), m1.getTotalRating());
        }
    });
    private Comparator<UiMinor> comparator;
    Sort(Comparator<UiMinor> comparator) {
        this.comparator = comparator;
    }

    public Comparator<UiMinor> getComparator() {
        return comparator;
    }
}

