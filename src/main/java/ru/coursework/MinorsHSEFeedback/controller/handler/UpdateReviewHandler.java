package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.mapper.UiReviewMapper;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

@Component
@RequiredArgsConstructor
public class UpdateReviewHandler {
    private final ReviewService reviewService;
    private final UiReviewMapper reviewMapper;

    @Transactional
    public UiReview handle(UpdateReviewRequest request) {
        return reviewMapper.apply(reviewService.updateReview(request));
    }
}
