package ru.coursework.MinorsHSEFeedback.service;

import ru.coursework.MinorsHSEFeedback.db.Like;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;

import java.util.Set;

public interface LikeService {
    Like addLike(AddLikeRequest request);
    boolean deleteLike(Long id, String email);
    Set<Like> getLikes(Long reviewId);
}
