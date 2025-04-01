package ru.coursework.MinorsHSEFeedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Result;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiReview;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ResultRepository resultRepository;
    private final MinorRepository minorRepository;
    private final UserService userService;
    @Transactional
    public UiReview createReview(CreateReviewRequest request) {
        checkCanCreateReview(request.getEmail());
        User user = userService.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long minorId = minorRepository.findByTitle(request.getMinorTitle());
        Review review = Review.builder()
                .userId(user.getId())
                .minorId(minorId)
                .body(request.getBody())
                .difficultyMark(request.getDifficultyMark())
                .interestMark(request.getInterestMark())
                .timeConsumptionMark(request.getTimeConsumptionMark())
                .totalMark(request.getTotalMark())
                .createDate(LocalDate.now())
                .build();
        reviewRepository.save(review);
        user.setCount(user.getCount() + 1);
        userService.save(user);
        Result result = resultRepository.findByMinorId(minorId).orElseThrow();
        result.setReviewsCount(result.getReviewsCount() + 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() + request.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() + request.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() + request.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() + request.getTotalMark());
        resultRepository.save(result);

        return new UiReview(review.getId(), user.getName(), request.getMinorTitle(), request.getBody(), request.getDifficultyMark(), request.getInterestMark(),
                    request.getTimeConsumptionMark(), request.getTotalMark(), review.getCreateDate());
    }

    @Transactional
    public UiReview updateReview(UpdateReviewRequest request) {
        checkCanUpdateOrDeleteReview(request.getReviewId(), request.getEmail());
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ReviewException("Review not found"));
        Result result = resultRepository.findByMinorId(review.getMinorId()).orElseThrow();
        if (request.getPatch().getBody() != null) {
            review.setBody(request.getPatch().getBody());
        }
        if (request.getPatch().getDifficultyMark() != null) {
            int tmp = review.getDifficultyMark();
            review.setDifficultyMark(request.getPatch().getDifficultyMark());
            result.setDifficultyMarkSum(result.getDifficultyMarkSum() - tmp + request.getPatch().getDifficultyMark());
        }
        if (request.getPatch().getInterestMark() != null) {
            int tmp = review.getInterestMark();
            review.setInterestMark(request.getPatch().getInterestMark());
            result.setInterestMarkSum(result.getInterestMarkSum() - tmp + request.getPatch().getInterestMark());
        }
        if (request.getPatch().getTimeConsumptionMark() != null) {
            int tmp = review.getTimeConsumptionMark();
            review.setTimeConsumptionMark(request.getPatch().getTimeConsumptionMark());
            result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() - tmp + request.getPatch().getTimeConsumptionMark());
        }
        if (request.getPatch().getTotalMark() != null) {
            int tmp = review.getTotalMark();
            review.setTotalMark(request.getPatch().getTotalMark());
            result.setTotalMarkSum(result.getTotalMarkSum() - tmp + request.getPatch().getTotalMark());
        }
        reviewRepository.save(review);
        resultRepository.save(result);
        String title = minorRepository.findById(review.getMinorId()).orElseThrow().getTitle();
        String userName = userService.findByEmail(request.getEmail()).orElseThrow().getName();
        return new UiReview(review.getId(), userName, title, review.getBody(), review.getDifficultyMark(), review.getInterestMark(),
                review.getTimeConsumptionMark(), review.getTotalMark(), review.getCreateDate());
    }

    @Transactional
    public boolean deleteReview(Long id, String email) {
        checkCanUpdateOrDeleteReview(id, email);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Review not found"));
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Result result = resultRepository.findByMinorId(review.getMinorId()).orElseThrow();
        user.setCount(user.getCount() - 1);
        result.setReviewsCount(result.getReviewsCount() - 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() - review.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() - review.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() - review.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() - review.getTotalMark());
        userService.save(user);
        resultRepository.save(result);
        reviewRepository.delete(review);
        //TO DO: удалить инфу о лайках и комментах
        return true;
    }

    private void checkCanCreateReview(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getCount() > 4) {
            throw new ReviewException("Превышен лимит количества отзывов для пользователя");
        }
    }

    private void checkCanUpdateOrDeleteReview(Long id, String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewException("Review not found"));
        if(!review.getUserId().equals(user.getId())) {
            throw new ReviewException("Текущий пользователь не является создателем данного отзыва");
        }
    }
}
