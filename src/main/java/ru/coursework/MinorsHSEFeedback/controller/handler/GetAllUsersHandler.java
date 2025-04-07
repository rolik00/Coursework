package ru.coursework.MinorsHSEFeedback.controller.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUsersHandler {
    private final UserService userService;
    private final UiUserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UiUser> handle() {
        List<User> users = userService.findAll();
        // здесь будет сортировка
        return users.stream().map(userMapper::apply).toList();
    }

}
