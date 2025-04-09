package ru.coursework.MinorsHSEFeedback.enums;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.coursework.MinorsHSEFeedback.enums.Sort.DIFFICULTY_SORT;
import static ru.coursework.MinorsHSEFeedback.enums.Sort.INTEREST_SORT;
import static ru.coursework.MinorsHSEFeedback.enums.Sort.TIME_CONSUMPTION_SORT;
import static ru.coursework.MinorsHSEFeedback.enums.Sort.TOTAL_SORT;

public class SortTest {
    @Test
    @DisplayName("Успешная сортировка по сложности")
    public void difficultySortTest() {
        UiMinor minor1 = Instancio.create(UiMinor.class);
        minor1.setDifficultyRating(1.5f);
        minor1.setInterestRating(2.25f);
        minor1.setTimeConsumptionRating(1f);
        minor1.setTotalRating(4.2f);

        UiMinor minor2 = Instancio.create(UiMinor.class);
        minor2.setDifficultyRating(1.51f);
        minor2.setInterestRating(2.25f);
        minor2.setTimeConsumptionRating(5f);
        minor2.setTotalRating(3.8f);

        var minors = new ArrayList<>(Arrays.asList(minor1, minor2));

        minors.sort(DIFFICULTY_SORT.getComparator());

        assertEquals(minor2, minors.get(0));
        assertEquals(minor1, minors.get(1));
    }

    @Test
    @DisplayName("Успешная сортировка по интересности")
    public void interestSortTest() {
        UiMinor minor1 = Instancio.create(UiMinor.class);
        minor1.setDifficultyRating(1.5f);
        minor1.setInterestRating(2.25f);
        minor1.setTimeConsumptionRating(1f);
        minor1.setTotalRating(4.2f);

        UiMinor minor2 = Instancio.create(UiMinor.class);
        minor2.setDifficultyRating(1.51f);
        minor2.setInterestRating(2.25f);
        minor2.setTimeConsumptionRating(5f);
        minor2.setTotalRating(3.8f);

        var minors = new ArrayList<>(Arrays.asList(minor1, minor2));

        minors.sort(INTEREST_SORT.getComparator());

        assertEquals(minor1, minors.get(0));
        assertEquals(minor2, minors.get(1));
    }

    @Test
    @DisplayName("Успешная сортировка по трудозатратности")
    public void timeConsumptionSortTest() {
        UiMinor minor1 = Instancio.create(UiMinor.class);
        minor1.setDifficultyRating(1.5f);
        minor1.setInterestRating(2.25f);
        minor1.setTimeConsumptionRating(1f);
        minor1.setTotalRating(4.2f);

        UiMinor minor2 = Instancio.create(UiMinor.class);
        minor2.setDifficultyRating(1.51f);
        minor2.setInterestRating(2.25f);
        minor2.setTimeConsumptionRating(5f);
        minor2.setTotalRating(3.8f);

        var minors = new ArrayList<>(Arrays.asList(minor1, minor2));

        minors.sort(TIME_CONSUMPTION_SORT.getComparator());

        assertEquals(minor2, minors.get(0));
        assertEquals(minor1, minors.get(1));
    }

    @Test
    @DisplayName("Успешная сортировка по общей оценке")
    public void totalSortTest() {
        UiMinor minor1 = Instancio.create(UiMinor.class);
        minor1.setDifficultyRating(1.5f);
        minor1.setInterestRating(2.25f);
        minor1.setTimeConsumptionRating(1f);
        minor1.setTotalRating(4.2f);

        UiMinor minor2 = Instancio.create(UiMinor.class);
        minor2.setDifficultyRating(1.51f);
        minor2.setInterestRating(2.25f);
        minor2.setTimeConsumptionRating(5f);
        minor2.setTotalRating(3.8f);

        var minors = new ArrayList<>(Arrays.asList(minor1, minor2));

        minors.sort(TOTAL_SORT.getComparator());

        assertEquals(minor1, minors.get(0));
        assertEquals(minor2, minors.get(1));
    }
}
