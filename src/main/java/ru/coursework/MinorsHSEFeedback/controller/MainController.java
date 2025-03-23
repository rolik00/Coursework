package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.enums.Sort;
import ru.coursework.MinorsHSEFeedback.service.MinorService;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@Slf4j
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private MinorService minorService;

    @GetMapping("")
    public Integer viewHomePage() {
        return 1;
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping("/users")
    public List<UiUser> listUsers() {
        return userService.findAll();
    }

    @Operation(summary = "Получить все майноры")
    @GetMapping("/minors")
    public List<UiMinor> getMinors() {
        return minorService.findAllMinors();
    }

    @Operation(summary = "Получить все майноры заданным категориям")
    @GetMapping("/categories")
    public List<UiMinor> getMinorsByCategory(@RequestParam(name = "categoryIds")  Set<Long> categoryIds) {
        return minorService.findAllMinorsByCategoryIds(categoryIds);
    }

    @Operation(summary = "Отсортировать все полученные майноры по одному из рейтингов")
    @GetMapping("/minors_sort")
    public List<UiMinor> getSortMinors(@RequestParam(name = "comparator") String comparator) {
        return minorService.findSortedAllMinors(Sort.valueOf(comparator).getComparator());
    }

    @Operation(summary = "Отсортировать все полученные по заданным категориям майноры")
    @GetMapping("/categories_sort")
    public List<UiMinor> getSortMinorsByCategory(
            @RequestParam(name = "categoryIds") Set<Long> categoryIds,
            @RequestParam(name = "comparator") String comparator) {
        return minorService.findSortedAllMinorsByCategoryIds(categoryIds, Sort.valueOf(comparator).getComparator());
    }

    @Operation(summary = "Получить майноры по id для сравнительной таблицы")
    @GetMapping("/comparison_table")
    public List<UiMinor> getMinorsForComparisonTable(
            @RequestParam(name = "ids") Set<Long> ids) {
        return minorService.getMinorsForComparisonTable(ids);
    }
}
