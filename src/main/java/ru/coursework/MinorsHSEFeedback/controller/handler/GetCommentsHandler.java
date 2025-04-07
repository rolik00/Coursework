package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.mapper.UiCommentMapper;
import ru.coursework.MinorsHSEFeedback.service.CommentService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetCommentsHandler {
    private final CommentService commentService;
    private final UiCommentMapper commentMapper;

    @Transactional(readOnly = true)
    public Set<UiComment> handle(Long reviewId) {
        return commentService.getComments(reviewId).stream().map(commentMapper::apply).collect(Collectors.toSet());
    }
}