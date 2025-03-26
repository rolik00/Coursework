package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.DeleteReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

@RestController
@RequestMapping("/minor")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Создать отзыв на майнор")
    @PostMapping("/review")
    public UiReview createReview(@RequestBody CreateReviewRequest request) {
        return reviewService.createReview(request);
    }

    @Operation(summary = "Обновить отзыв на майнор")
    @PatchMapping("/review")
    public UiReview updateReview(@RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(request);
    }

    @Operation(summary = "Удалить отзыв на майнор")
    @DeleteMapping("/review")
    public boolean deleteReview(@RequestBody DeleteReviewRequest request) {
        return reviewService.deleteReview(request.getReviewId(), request.getEmail());
    }
}
