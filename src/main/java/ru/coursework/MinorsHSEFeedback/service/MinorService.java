package ru.coursework.MinorsHSEFeedback.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

import java.util.List;

@Service
@Transactional
public class MinorService {
    @Autowired
    private MinorRepository minorRepository;

    public List<Minor> findAll() {
        return minorRepository.findAll();
    }
}
