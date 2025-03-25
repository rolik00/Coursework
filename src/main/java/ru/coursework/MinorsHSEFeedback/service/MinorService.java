package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
public class MinorService {
    @Autowired
    private MinorRepository minorRepository;
    @Autowired
    private UiMinorMapper uiMinorMapper;

    @Transactional(readOnly = true)
    public UiMinor getMinor(Long id) {
        Minor minor = minorRepository.findById(id).orElseThrow();
        return uiMinorMapper.apply(minor);
    }

    @Transactional(readOnly = true)
    public List<UiMinor> findAllMinors() {
        List<Minor> minors = minorRepository.findAll();
        return minors.stream().map(uiMinorMapper::apply).toList();
    }

    @Transactional(readOnly = true)
    public List<UiMinor> findAllMinorsByCategoryIds(Set<Long> categoryIds) {
        List<Minor> minors = minorRepository.findAllByCategoryIds(categoryIds);
        return minors.stream().map(uiMinorMapper::apply).toList();
    }

    @Transactional(readOnly = true)
    public List<UiMinor> findSortedAllMinors(Comparator<UiMinor> comparator) {
        List<Minor> minors = minorRepository.findAll();
        List<UiMinor> uiMinors = new ArrayList<>(minors.stream().map(uiMinorMapper::apply).toList());
        uiMinors.sort(comparator);
        return uiMinors;
    }

    @Transactional(readOnly = true)
    public List<UiMinor> findSortedAllMinorsByCategoryIds(Set<Long> categoryIds, Comparator<UiMinor> comparator) {
        List<Minor> minors = minorRepository.findAllByCategoryIds(categoryIds);
        List<UiMinor> uiMinors = new ArrayList<>(minors.stream().map(uiMinorMapper::apply).toList());
        uiMinors.sort(comparator);
        return uiMinors;
    }

    @Transactional(readOnly = true)
    public List<UiMinor> getMinorsForComparisonTable(Set<Long> ids) {
        return ids.stream()
                .map(minorRepository::findById)
                .filter(Optional::isPresent)
                .map(minor -> uiMinorMapper.apply(minor.get()))
                .toList();

    }
}
