package ru.coursework.MinorsHSEFeedback.mapper;

import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;

public interface UiReviewMapper {
    public UiReview apply(Review review);
}
