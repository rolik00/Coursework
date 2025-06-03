package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetAllMinorsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetAllSortedMinorsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetAllUsersHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetMinorsByCategoriesHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetMinorsByIdsHandler;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetSortedMinorsByCategoriesHandler;
import ru.coursework.MinorsHSEFeedback.db.ui.MinorTitleInfo;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.enums.Sort;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final GetAllUsersHandler getAllUsersHandler;
    private final GetAllMinorsHandler getAllMinorsHandler;
    private final GetMinorsByCategoriesHandler getMinorsByCategoriesHandler;
    private final GetAllSortedMinorsHandler getAllSortedMinorsHandler;
    private final GetSortedMinorsByCategoriesHandler getSortedMinorsByCategoriesHandler;
    private final GetMinorsByIdsHandler getMinorsByIdsHandler;
    private final MinorService minorService;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping("/users")
    public List<UiUser> listUsers() {
        return getAllUsersHandler.handle();
    }

    @Operation(summary = "Получить все майноры")
    @GetMapping("/minors")
    public List<UiMinor> getMinors() {
        return getAllMinorsHandler.handle();
    }

    @Operation(summary = "Получить все майноры заданным категориям")
    @GetMapping("/categories")
    public List<UiMinor> getMinorsByCategory(@RequestParam(name = "categoryIds") Set<Long> categoryIds) {
        return getMinorsByCategoriesHandler.handle(categoryIds);
    }

    @Operation(summary = "Отсортировать все полученные майноры по одному из рейтингов")
    @GetMapping("/minors_sort")
    public List<UiMinor> getSortMinors(@RequestParam(name = "comparator") String comparator) {
        return getAllSortedMinorsHandler.handle(Sort.valueOf(comparator).getComparator());
    }

    @Operation(summary = "Отсортировать все полученные по заданным категориям майноры")
    @GetMapping("/categories_sort")
    public List<UiMinor> getSortMinorsByCategories(
            @RequestParam(name = "categoryIds") Set<Long> categoryIds,
            @RequestParam(name = "comparator") String comparator) {
        return getSortedMinorsByCategoriesHandler.handle(categoryIds, Sort.valueOf(comparator).getComparator());
    }

    @Operation(summary = "Получить майноры по id для сравнительной таблицы")
    @GetMapping("/comparison_table")
    public List<UiMinor> getMinorsForComparisonTable(
            @RequestParam(name = "ids") Set<Long> ids) {
        return getMinorsByIdsHandler.handle(ids);
    }

    @Operation(summary = "Получить названия всех майноров")
    @GetMapping("/minors_title")
    public List<MinorTitleInfo> getMinorTitleInfo() {
        return minorService.getMinorTitleInfo();
    }
}
