package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.mapper.UiCommentMapper;
import ru.coursework.MinorsHSEFeedback.request.UpdateCommentRequest;
import ru.coursework.MinorsHSEFeedback.service.CommentService;

@Component
@RequiredArgsConstructor
public class UpdateCommentHandler {
    private final CommentService commentService;
    private final UiCommentMapper commentMapper;

    @Transactional
    public UiComment handle(UpdateCommentRequest request) {
        return commentMapper.apply(commentService.updateComment(request));
    }
}
