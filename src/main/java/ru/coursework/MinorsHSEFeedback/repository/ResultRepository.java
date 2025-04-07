package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Result;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByMinorId(Long minorId);
}

