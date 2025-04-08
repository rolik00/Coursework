package ru.coursework.MinorsHSEFeedback.service;

import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateResetPasswordToken(String token, String email);
    User getByResetPasswordToken(String token);
    void updatePassword(User user, String newPassword);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    void save(User user);
    User updateUser(UpdateUserRequest request);
}
