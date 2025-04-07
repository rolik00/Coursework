package ru.coursework.MinorsHSEFeedback.service;

import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;

import java.util.Set;

public interface ReviewService {
    Review createReview(CreateReviewRequest request);
    Review updateReview(UpdateReviewRequest request);
    boolean deleteReview(Long id, String email);
    Set<Review> getReviews(Long minorId);
}
