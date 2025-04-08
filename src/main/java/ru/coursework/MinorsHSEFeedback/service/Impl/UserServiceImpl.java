package ru.coursework.MinorsHSEFeedback.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.request.UpdateUserRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.List;
import java.util.Optional;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.IS_NOT_HSE_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MinorRepository minorRepository;

    @Override
    @Transactional
    public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        user.setResetPasswordToken(token);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    @Transactional
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(UpdateUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        if (request.getPatch().getName() != null) {
            user.setName(request.getPatch().getName());
        }
        if (request.getPatch().getMinorTitle() != null) {
            Long minorId = minorRepository.findByTitle(request.getPatch().getMinorTitle());
            user.setMinorId(minorId);
        }
        if (request.getPatch().getCourseTitle() != null) {
            user.setCourseTitle(request.getPatch().getCourseTitle());
        }
        if (request.getPatch().getEmail() != null) {
            checkCanUpdateEmail(request.getPatch().getEmail());
            user.setEmail(request.getPatch().getEmail());
        }
        userRepository.save(user);
        return user;
    }

    private void checkCanUpdateEmail(String newEmail) {
        if (!newEmail.endsWith("@edu.hse.ru")) {
            throw new RuntimeException(IS_NOT_HSE_ERROR.getTitle());
        }
    }
}

