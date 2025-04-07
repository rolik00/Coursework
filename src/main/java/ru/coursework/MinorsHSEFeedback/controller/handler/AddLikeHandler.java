package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiLike;
import ru.coursework.MinorsHSEFeedback.mapper.UiLikeMapper;
import ru.coursework.MinorsHSEFeedback.request.AddLikeRequest;
import ru.coursework.MinorsHSEFeedback.service.LikeService;

@Component
@RequiredArgsConstructor
public class AddLikeHandler {
    private final LikeService likeService;
    private final UiLikeMapper likeMapper;

    @Transactional
    public UiLike handle(AddLikeRequest request) {
        return likeMapper.apply(likeService.addLike(request));
    }
}
