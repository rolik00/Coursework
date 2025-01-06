package ru.coursework.MinorsHSEFeedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.MinorsHSEFeedback.db.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
    int findReviewsCountByMinorId (Long minorId);
    int findDifficultyMarkSumByMinorId (Long minorId);
    int findInterestMarkSumByMinorId (Long minorId);
    int findTimeConsumptionMarkSumByMinorId (Long minorId);
    int findTotalMarkSumByMinorId (Long minorId);
}

