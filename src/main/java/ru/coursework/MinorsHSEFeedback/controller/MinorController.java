package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.service.MinorService;
import ru.coursework.MinorsHSEFeedback.service.ReviewService;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.COUNT_MORE_FOUR;

@Controller
public class MinorController {
    @Autowired
    private MinorService minorService;
    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Получить майнор по id")
    @GetMapping("/minor/{minorId}")
    public UiMinor getMinors(@PathVariable Long id) {
        return minorService.getMinor(id);
    }

    @Operation(summary = "Создать отзыв на майнор")
    @PostMapping("/minor/reviews")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request) {
        UiReview review = reviewService.createReview(request);
        if (review != null) return ResponseEntity.ok(review);
        return ResponseEntity.badRequest().body(COUNT_MORE_FOUR);
    }

}
