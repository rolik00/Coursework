package ru.coursework.MinorsHSEFeedback.mapper.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.Comment;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiComment;
import ru.coursework.MinorsHSEFeedback.mapper.UiCommentMapper;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UiCommentMapperImpl implements UiCommentMapper {
    private final UserRepository userRepository;
    @Override
    public UiComment apply (Comment comment) {
        if (comment == null) {
            return null;
        }
        User user = userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UiComment uiComment = new UiComment();
        uiComment.setId(comment.getId());
        uiComment.setBody(comment.getBody());
        uiComment.setReviewId(comment.getReviewId());
        uiComment.setUserName(user.getName());
        uiComment.setCreateDate(comment.getCreateDate());
        uiComment.setParentId(comment.getParentId());
        return uiComment;
    }
}
