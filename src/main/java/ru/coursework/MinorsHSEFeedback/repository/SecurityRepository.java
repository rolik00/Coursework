package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.coursework.MinorsHSEFeedback.db.Security;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityRepository extends JpaRepository<Security, UUID> {
    Optional<Security> findByLogin(String login);
}
