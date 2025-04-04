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
import ru.coursework.MinorsHSEFeedback.mapper.UiReviewMapper;
import ru.coursework.MinorsHSEFeedback.repository.CommentRepository;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.ResultRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.CreateReviewRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdateReviewRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ResultRepository resultRepository;
    private final MinorRepository minorRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UiReviewMapper reviewMapper;

    private final Comparator<UiReview> comparatorByValue = (r1, r2) -> Float.compare(r2.getValue(), r1.getValue());
    private final Comparator<UiReview> comparatorByDate = Comparator.comparing(UiReview::getCreateDate).reversed();
    @Transactional
    public UiReview createReview(CreateReviewRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Long minorId = minorRepository.findByTitle(request.getMinorTitle());
        checkCanCreateReview(request.getEmail(), minorId);
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
        userRepository.save(user);
        Result result = resultRepository.findByMinorId(minorId).orElseThrow();
        result.setReviewsCount(result.getReviewsCount() + 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() + request.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() + request.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() + request.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() + request.getTotalMark());
        resultRepository.save(result);

        return reviewMapper.apply(review);
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
        return reviewMapper.apply(review);
    }

    @Transactional
    public boolean deleteReview(Long id, String email) {
        checkCanUpdateOrDeleteReview(id, email);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Review not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Result result = resultRepository.findByMinorId(review.getMinorId()).orElseThrow();
        user.setCount(user.getCount() - 1);
        result.setReviewsCount(result.getReviewsCount() - 1);
        result.setDifficultyMarkSum(result.getDifficultyMarkSum() - review.getDifficultyMark());
        result.setInterestMarkSum(result.getInterestMarkSum() - review.getInterestMark());
        result.setTimeConsumptionMarkSum(result.getTimeConsumptionMarkSum() - review.getTimeConsumptionMark());
        result.setTotalMarkSum(result.getTotalMarkSum() - review.getTotalMark());
        userRepository.save(user);
        resultRepository.save(result);
        reviewRepository.delete(review);
        likeRepository.deleteByReviewId(review.getId());
        commentRepository.deleteByReviewId(review.getId());
        return true;
    }

    @Transactional(readOnly = true)
    public List<UiReview> getReviews(Long minorId) {
        Set<Review> reviews = reviewRepository.getReviews(minorId);
        List<UiReview> uiReviews = new ArrayList<>(reviews.stream().map(reviewMapper::apply).toList());
        uiReviews.sort(comparatorByValue);
        return uiReviews;
    }

    @Transactional(readOnly = true)
    public List<UiReview> getReviewsSortByDate(Long minorId) {
        Set<Review> reviews = reviewRepository.getReviews(minorId);
        List<UiReview> uiReviews = new ArrayList<>(reviews.stream().map(reviewMapper::apply).toList());
        uiReviews.sort(comparatorByDate);
        return uiReviews;
    }

    private void checkCanCreateReview(String email, Long minorId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Optional<Review> review = reviewRepository.findReviewByUserIdAndMinorId(user.getId(), minorId);
        if (review.isPresent()) {
            throw new ReviewException("Пользователь уже писал отзыв для этого майнора");
        }
        if (user.getCount() >= 4) {
            throw new ReviewException("Превышен лимит количества отзывов для пользователя");
        }
    }

    private void checkCanUpdateOrDeleteReview(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewException("Review not found"));
        if(!review.getUserId().equals(user.getId())) {
            throw new ReviewException("Текущий пользователь не является создателем данного отзыва");
        }
    }
}
