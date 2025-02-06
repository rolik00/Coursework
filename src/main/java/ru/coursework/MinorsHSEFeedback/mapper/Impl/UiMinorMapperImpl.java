package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;

@Component
public class UiMinorMapperImpl implements UiMinorMapper {
    @Autowired
    private ResultRepository resultRepository;
    @Override
    public UiMinor apply(Minor minor) {
        if (minor == null) {
            return null;
        }

        UiMinor uiMinor = new UiMinor();
        uiMinor.setId(minor.getId());
        uiMinor.setCategoryId(minor.getCategoryId());
        uiMinor.setTitle(minor.getTitle());

        Result result = resultRepository.findByMinorId(minor.getId()).orElseThrow();
        float difficultyRating = 0;
        float interestRating = 0;
        float timeConsumptionRating = 0;
        float totalRating = 0;

        if (result.getReviewsCount() != 0) {
            difficultyRating = result.getDifficultyMarkSum() / result.getReviewsCount();
            interestRating = result.getInterestMarkSum() / result.getReviewsCount();
            timeConsumptionRating = result.getTimeConsumptionMarkSum() / result.getReviewsCount();
            totalRating = result.getTotalMarkSum() / result.getReviewsCount();
        }

        uiMinor.setDifficultyRating(difficultyRating);
        uiMinor.setInterestRating(interestRating);
        uiMinor.setTimeConsumptionRating(timeConsumptionRating);
        uiMinor.setTotalRating(totalRating);

        return uiMinor;
    }
}
