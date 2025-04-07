package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.mapper.UiReviewMapper;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetReviewsSortedByDateHandler {
    private final ReviewService reviewService;
    private final UiReviewMapper reviewMapper;
    private final Comparator<UiReview> comparatorByDate = Comparator.comparing(UiReview::getCreateDate).reversed();

    @Transactional(readOnly = true)
    public List<UiReview> handle(Long minorId) {
        List<UiReview> uiReviews = new ArrayList<>(reviewService.getReviews(minorId).stream().map(reviewMapper::apply).toList());
        uiReviews.sort(comparatorByDate);
        return uiReviews;
    }
}