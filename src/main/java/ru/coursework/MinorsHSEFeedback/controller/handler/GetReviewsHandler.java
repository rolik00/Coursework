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
public class GetReviewsHandler {
    private final ReviewService reviewService;
    private final UiReviewMapper reviewMapper;
    private final Comparator<UiReview> comparatorByValue = (r1, r2) -> Float.compare(r2.getValue(), r1.getValue());

    @Transactional(readOnly = true)
    public List<UiReview> handle(Long minorId) {
        List<UiReview> uiReviews = new ArrayList<>(reviewService.getReviews(minorId).stream().map(reviewMapper::apply).toList());
        uiReviews.sort(comparatorByValue);
        return uiReviews;
    }
}
