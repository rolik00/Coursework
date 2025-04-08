package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.mapper.UiReviewMapper;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetUserReviewsHandler {
    private final ReviewService reviewService;
    private final UiReviewMapper reviewMapper;

    public Set<UiReview> handle(String email) {
        return reviewService.getReviewsByUser(email).stream()
                .map(reviewMapper::apply)
                .collect(Collectors.toSet());
    }
}
