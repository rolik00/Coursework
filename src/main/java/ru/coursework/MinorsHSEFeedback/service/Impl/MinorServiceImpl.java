package ru.coursework.MinorsHSEFeedback.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.MinorTitleInfo;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class MinorServiceImpl implements MinorService {
    private final MinorRepository minorRepository;

    @Override
    @Transactional(readOnly = true)
    public Minor getMinor(Long id) {
        return minorRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Minor> findAllMinors() {
        return minorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Minor> findAllMinorsByCategoryIds(Set<Long> categoryIds) {
        return minorRepository.findAllByCategoryIds(categoryIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Minor> getMinorsByIds(Set<Long> ids) {
        return ids.stream()
                .map(minorRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public Long getMinorIdByTitle(String title) {
        return minorRepository.findByTitle(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MinorTitleInfo> getMinorTitleInfo() {
        return minorRepository.getAllMinorTitles();
    }
}
