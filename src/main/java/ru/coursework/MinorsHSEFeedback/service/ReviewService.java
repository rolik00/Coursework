package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;

import java.time.LocalDateTime;

@Service
@Transactional
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private MinorRepository minorRepository;
    @Autowired
    private UserService userService;
    @Transactional
    public UiReview createReview(CreateReviewRequest request) {
        if (checkCanCreateReview(request.getEmail())) {
            Long userId = userService.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found")).getId();
            Long minorId = minorRepository.findByTitle(request.getMinorTitle());
            Review review = Review.builder()
                    .userId(userId)
                    .minorId(minorId)
                    .body(request.getBody())
                    .difficultyMark(request.getDifficultyMark())
                    .interestMark(request.getInterestMark())
                    .timeConsumptionMark(request.getTimeConsumptionMark())
                    .totalMark(request.getTotalMark())
                    .createDate(LocalDateTime.now())
                    .build();
            reviewRepository.save(review);
            Result result = resultRepository.findByMinorId(minorId).orElseThrow();
            result.setReviewsCount(result.getReviewsCount() + 1);
            result.setDifficultyMarkSum(result.getDifficultyMarkSum() + request.getDifficultyMark());
            result.setInterestMarkSum(result.getInterestMarkSum() + request.getInterestMark());
            result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() + request.getTimeConsumptionMark());
            result.setTotalMarkSum(result.getTotalMarkSum() + request.getTotalMark());
            resultRepository.save(result);

            return new UiReview(request.getEmail(), request.getMinorTitle(), request.getBody(), request.getDifficultyMark(), request.getInterestMark(),
                    request.getTimeConsumptionMark(), request.getTotalMark(), review.getCreateDate());
        }
        return null;
    }

    private boolean checkCanCreateReview(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getCount() <= 4;
    }
}
