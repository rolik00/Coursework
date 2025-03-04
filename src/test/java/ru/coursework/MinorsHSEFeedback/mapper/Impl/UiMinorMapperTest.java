package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.hibernate.annotations.Instantiator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mapping.InstanceCreatorMetadata;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;

import java.util.Optional;

public class UiMinorMapperTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private UiMinorMapperImpl uiMinorMapperImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testApplyWithNullMinor() {
        UiMinor result = uiMinorMapperImpl.apply(null);
        assertNull(result);
    }

    @Test
    public void testApplyWithValidMinor() {
        Minor minor = new Minor();
        minor.setId(1L);
        minor.setCategoryId(10L);
        minor.setTitle("Test Minor");

        Result result = new Result();
        result.setReviewsCount(2);
        result.setDifficultyMarkSum(8);
        result.setInterestMarkSum(6);
        result.setTimeConsumptionMarkSum(4);
        result.setTotalMarkSum(10);

        when(resultRepository.findByMinorId(1L)).thenReturn(Optional.of(result));

        UiMinor uiMinor = uiMinorMapperImpl.apply(minor);

        assertNotNull(uiMinor);
        assertEquals(minor.getId(), uiMinor.getId());
        assertEquals(minor.getCategoryId(), uiMinor.getCategoryId());
        assertEquals(minor.getTitle(), uiMinor.getTitle());
        assertEquals(4.0f, uiMinor.getDifficultyRating());
        assertEquals(3.0f, uiMinor.getInterestRating());
        assertEquals(2.0f, uiMinor.getTimeConsumptionRating());
        assertEquals(5.0f, uiMinor.getTotalRating());
    }

    @Test
    public void testApplyWithNoReviews() {
        Minor minor = new Minor();
        minor.setId(1L);
        minor.setCategoryId(10L);
        minor.setTitle("Test Minor");

        Result result = new Result();
        result.setReviewsCount(0);
        result.setDifficultyMarkSum(0);
        result.setInterestMarkSum(0);
        result.setTimeConsumptionMarkSum(0);
        result.setTotalMarkSum(0);

        when(resultRepository.findByMinorId(1L)).thenReturn(Optional.of(result));

        UiMinor uiMinor = uiMinorMapperImpl.apply(minor);

        assertNotNull(uiMinor);
        assertEquals(minor.getId(), uiMinor.getId());
        assertEquals(minor.getCategoryId(), uiMinor.getCategoryId());
        assertEquals(minor.getTitle(), uiMinor.getTitle());
        assertEquals(0.0f, uiMinor.getDifficultyRating());
        assertEquals(0.0f, uiMinor.getInterestRating());
        assertEquals(0.0f, uiMinor.getTimeConsumptionRating());
        assertEquals(0.0f, uiMinor.getTotalRating());
    }

    @Test
    public void testApplyWithMissingResult() {
        Minor minor = new Minor();
        minor.setId(1L);
        minor.setCategoryId(10L);
        minor.setTitle("Test Minor");

        when(resultRepository.findByMinorId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            uiMinorMapperImpl.apply(minor);
        });
    }
}

