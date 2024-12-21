package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Security;

import java.util.Optional;
import java.util.UUID;

public interface SecurityRepository extends JpaRepository<Security, UUID> {
    Optional<Security> findByLogin(String login);
}
