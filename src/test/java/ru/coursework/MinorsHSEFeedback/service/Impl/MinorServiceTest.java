package ru.coursework.MinorsHSEFeedback.service.Impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.repository.MinorRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinorServiceTest {
    @Mock
    private MinorRepository minorRepository;
    @InjectMocks
    private MinorServiceImpl minorService;
    private Minor minor1;
    private Minor minor2;
    private Minor minor3;

    @BeforeEach
    public void setUp() {
        minor1 = new Minor();
        minor1.setId(1L);
        minor1.setTitle("Minor 1");
        minor1.setCategoryId(1L);

        minor2 = new Minor();
        minor2.setId(2L);
        minor2.setTitle("Minor 2");
        minor2.setCategoryId(2L);

        minor3 = new Minor();
        minor3.setId(3L);
        minor3.setTitle("Minor 3");
        minor3.setCategoryId(1L);
    }

    @Test
    @DisplayName("Успешное получение майнора по айди")
    public void testGetMinor() {
        Long id = 1L;

        when(minorRepository.findById(id)).thenReturn(Optional.of(minor1));

        Minor minor = minorService.getMinor(id);

        assertNotNull(minor);
        assertEquals(id, minor.getId());
        assertEquals(minor1.getTitle(), minor.getTitle());
    }

    @Test
    @DisplayName("Майнор не найден")
    public void testGetMinorNotFound() {
        Long id = 3L;

        when(minorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> minorService.getMinor(id));
    }

    @Test
    @DisplayName("Успешное получение всех майноров")
    public void testFindAllMinors() {
        when(minorRepository.findAll()).thenReturn(Arrays.asList(minor1, minor2, minor3));

        List<Minor> minors = minorService.findAllMinors();

        assertNotNull(minors);
        assertEquals(3, minors.size());
        assertEquals(minor1, minors.get(0));
        assertEquals(minor2, minors.get(1));
        assertEquals(minor3, minors.get(2));
    }

    @Test
    @DisplayName("Успешное получение всех майноров по категории")
    public void testFindAllMinorsByCategoryIds() {
        when(minorRepository.findAllByCategoryIds(Set.of(1L))).thenReturn(Arrays.asList(minor1, minor3));

        List<Minor> minors = minorService.findAllMinorsByCategoryIds(Set.of(1L));

        assertNotNull(minors);
        assertEquals(2, minors.size());
        assertEquals(minor1, minors.get(0));
        assertEquals(minor3, minors.get(1));
    }

    @Test
    @DisplayName("Успешное получение всех майноров по айди")
    public void testGetMinorsByIds() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 2L));

        when(minorRepository.findById(1L)).thenReturn(Optional.of(minor1));
        when(minorRepository.findById(2L)).thenReturn(Optional.of(minor2));

        List<Minor> minors = minorService.getMinorsByIds(ids);

        assertNotNull(minors);
        assertEquals(2, minors.size());
        assertEquals(minor1, minors.get(0));
        assertEquals(minor2, minors.get(1));
    }

    @Test
    @DisplayName("Успешное получение всех майноров по айди, некоторые из которых не существуют")
    public void testGetMinorsByIdsSomeNotFound() {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L, 4L));

        when(minorRepository.findById(1L)).thenReturn(Optional.of(minor1));
        when(minorRepository.findById(4L)).thenReturn(Optional.empty());

        List<Minor> minors = minorService.getMinorsByIds(ids);

        assertNotNull(minors);
        assertEquals(1, minors.size());
        assertEquals(minor1, minors.getFirst());
    }

    @Test
    @DisplayName("Успешное получение айди майнора по названию")
    public void testGetMinorIdByTitle() {
        when(minorRepository.findByTitle(minor1.getTitle())).thenReturn(minor1.getId());

        Long minorId = minorService.getMinorIdByTitle(minor1.getTitle());

        assertNotNull(minorId);
        assertEquals(minor1.getId(), minorId);
    }

    @Test
    @DisplayName("Майнор по названию не найден")
    public void testGetMinorIdByTitleNotFound() {
        String title = "Non-existent Minor";

        when(minorRepository.findByTitle(title)).thenReturn(null);

        Long minorId = minorService.getMinorIdByTitle(title);
        assertNull(minorId);
    }
}