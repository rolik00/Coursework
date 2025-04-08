package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

@Component
@RequiredArgsConstructor
public class UpdateUserHandler {
    private final UserService userService;
    private final UiUserMapper userMapper;

    public UiUser handle(UpdateUserRequest request) {
        return userMapper.apply(userService.updateUser(request));
    }
}
