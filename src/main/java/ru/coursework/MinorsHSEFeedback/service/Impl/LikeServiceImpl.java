package ru.coursework.MinorsHSEFeedback.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.db.Review;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.exceptions.LikeException;
import ru.coursework.MinorsHSEFeedback.exceptions.ReviewException;
import ru.coursework.MinorsHSEFeedback.repository.LikeRepository;
import ru.coursework.MinorsHSEFeedback.repository.ReviewRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;
import ru.coursework.MinorsHSEFeedback.service.LikeService;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Like addLike(AddLikeRequest request) {
        checkCanAddLike(request.getEmail(), request.getReviewId());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Like like = new Like();
        like.setValue(request.isValue());
        like.setReviewId(request.getReviewId());
        like.setUserId(user.getId());
        likeRepository.save(like);
        return like;
    }

    @Override
    @Transactional
    public boolean deleteLike(Long id, String email) {
        checkCanDeleteLike(id, email);
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeException("Лайк/дизлайк не найден"));
        likeRepository.delete(like);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Like> getLikes(Long reviewId) {
        return likeRepository.getLikes(reviewId);
    }

    private void checkCanAddLike(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException("Отзыв не найден"));
        Optional<Like> like = likeRepository.findLikeByUserIdAndReviewId(user.getId(), reviewId);
        if (like.isPresent()) {
            throw new LikeException("Пользователь уже поставил лайк или дизлайк на этот отзыв");
        }
        if (user.getId().equals(review.getUserId())) {
            throw new LikeException("Пользователь не может поставить лайк/дизлайк на свой отзыв");
        }
    }

    private void checkCanDeleteLike(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeException("Лайк/дизлайк не найден"));
        if(!like.getUserId().equals(user.getId())) {
            throw new LikeException("Текущий пользователь не ставил лайк на этот отзыв");
        }
    }
}
