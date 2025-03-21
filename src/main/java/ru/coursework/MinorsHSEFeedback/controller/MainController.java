package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.enums.Sort;
import ru.coursework.MinorsHSEFeedback.request.MinorsForCTRequest;
import ru.coursework.MinorsHSEFeedback.request.SortAllMinorsByCategoriesRequest;
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
    @PostMapping("/categories")
    public List<UiMinor> getMinorsByCategory(@RequestBody Set<Long> categoryIds) {
        return minorService.findAllMinorsByCategoryIds(categoryIds);
    }

    @Operation(summary = "Отсортировать все полученные майноры по одному из рейтингов")
    @PostMapping("/minors_sort")
    public List<UiMinor> getSortMinors(@RequestBody String comparator) {
        return minorService.findSortedAllMinors(Sort.valueOf(comparator).getComparator());
    }

    @Operation(summary = "Отсортировать все полученные по заданным категориям майноры")
    @PostMapping("/categories_sort")
    public List<UiMinor> getSortMinorsByCategory(@RequestBody SortAllMinorsByCategoriesRequest request) {
        log.info("getSortMinorsByCategory {}", request);
        return minorService.findSortedAllMinorsByCategoryIds(request.getCategoryIds(), Sort.valueOf(request.getComparator()).getComparator());
    }

    @Operation(summary = "Получить майноры по id для сравнительной таблицы")
    @PostMapping("/comparison_table")
    public List<UiMinor> getMinorsForComparisonTable(@RequestBody MinorsForCTRequest request) {
        return minorService.getMinorsForComparisonTable(request.getIds());
    }
}
