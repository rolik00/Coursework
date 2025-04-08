package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.service.UserService;

@Component
@RequiredArgsConstructor
public class GetUserByEmailHandler {
    private final UserService userService;
    private final UiUserMapper userMapper;

    @Transactional(readOnly = true)
    public UiUser handle(String email) {
        return userMapper.apply(userService.findByEmail(email).orElse(null));
    }
}
