package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
