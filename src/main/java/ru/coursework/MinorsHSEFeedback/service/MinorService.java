package ru.coursework.MinorsHSEFeedback.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
@Slf4j
public class MinorService {
    @Autowired
    private MinorRepository minorRepository;
    @Autowired
    private UiMinorMapper uiMinorMapper;

    public UiMinor getMinor(Long id) {
        Minor minor = minorRepository.findById(id).orElseThrow();
        return uiMinorMapper.apply(minor);
    }

    public List<UiMinor> findAllMinors() {
        List<Minor> minors = minorRepository.findAll();
        log.info("invoke findAll");
        return minors.stream().map(uiMinorMapper::apply).toList();
    }

    public List<UiMinor> findAllMinorsByCategoryIds(Set<Long> categoryIds) {
        List<Minor> minors = minorRepository.findAllByCategoryIds(categoryIds);
        return minors.stream().map(uiMinorMapper::apply).toList();
    }

    public List<UiMinor> findSortedAllMinors(Comparator<UiMinor> comparator) {
        List<Minor> minors = minorRepository.findAll();
        List<UiMinor> uiMinors = new ArrayList<>(minors.stream().map(uiMinorMapper::apply).toList());
        uiMinors.sort(comparator);
        return uiMinors;
    }

    public List<UiMinor> findSortedAllMinorsByCategoryIds(Set<Long> categoryIds, Comparator<UiMinor> comparator) {
        List<Minor> minors = minorRepository.findAllByCategoryIds(categoryIds);
        List<UiMinor> uiMinors = new ArrayList<>(minors.stream().map(uiMinorMapper::apply).toList());
        uiMinors.sort(comparator);
        return uiMinors;
    }

    public List<UiMinor> getMinorsForComparisonTable(Set<Long> ids) {
        return ids.stream()
                .map(minorRepository::findById)
                .filter(Optional::isPresent)
                .map(minor -> uiMinorMapper.apply(minor.get()))
                .toList();

    }

}
