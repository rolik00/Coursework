package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
