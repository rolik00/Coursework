package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Minor;

public interface MinorRepository extends JpaRepository<Minor, Long> {
}
