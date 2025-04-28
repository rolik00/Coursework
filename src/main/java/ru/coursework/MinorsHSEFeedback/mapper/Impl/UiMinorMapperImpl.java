package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.repository.CategoryRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;

@Component
@RequiredArgsConstructor
public class UiMinorMapperImpl implements UiMinorMapper {
    private final ResultRepository resultRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public UiMinor apply(Minor minor) {
        if (minor == null) {
            return null;
        }
        String categoryTitle = categoryRepository.findTitleById(minor.getCategoryId());
        UiMinor uiMinor = new UiMinor();
        uiMinor.setId(minor.getId());
        uiMinor.setCategoryTitle(categoryTitle);
        uiMinor.setTitle(minor.getTitle());
        uiMinor.setLink(minor.getLink());

        Result result = resultRepository.findByMinorId(minor.getId()).orElseThrow();
        float difficultyRating = 0;
        float interestRating = 0;
        float timeConsumptionRating = 0;
        float totalRating = 0;

        if (result.getReviewsCount() != 0) {
            difficultyRating = (float) result.getDifficultyMarkSum() / result.getReviewsCount();
            interestRating = (float) result.getInterestMarkSum() / result.getReviewsCount();
            timeConsumptionRating = (float) result.getTimeConsumptionMarkSum() / result.getReviewsCount();
            totalRating = (float) result.getTotalMarkSum() / result.getReviewsCount();
        }

        uiMinor.setDifficultyRating(difficultyRating);
        uiMinor.setInterestRating(interestRating);
        uiMinor.setTimeConsumptionRating(timeConsumptionRating);
        uiMinor.setTotalRating(totalRating);
        uiMinor.setReviewCount(result.getReviewsCount());

        return uiMinor;
    }
}
