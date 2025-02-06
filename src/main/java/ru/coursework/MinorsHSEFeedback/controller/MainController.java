package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.enums.Sort;
import ru.coursework.MinorsHSEFeedback.request.SortAllMinorsByCategoriesRequest;
import ru.coursework.MinorsHSEFeedback.service.MinorService;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.List;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private MinorService minorService;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UiUser> users = userService.findAll();
        model.addAttribute("listUsers", users);
        return "users";
    }

    @Operation(summary = "Получить все майноры")
    @GetMapping("/minors")
    public String getMinors(Model model) {
        List<UiMinor> minors = minorService.findAllMinors();
        model.addAttribute("minors", minors);
        return "minors";
    }

    @Operation(summary = "Получить все майноры заданным категориям")
    @GetMapping("/categories")
    public String getMinorsByCategory(@RequestParam Set<Long> categoryIds, Model model) {
        List<UiMinor> minors = minorService.findAllMinorsByCategoryIds(categoryIds);
        model.addAttribute("minors", minors);
        return "minors";
    }

    @Operation(summary = "Отсортировать все полученные майноры по одному из рейтингов")
    @GetMapping("/minors_sort")
    public String getSortMinors(@RequestParam String comparator, Model model) {
        List<UiMinor> minors = minorService.findSortedAllMinors(Sort.valueOf(comparator).getComparator());
        model.addAttribute("minors", minors);
        return "minors";
    }

    @Operation(summary = "Отсортировать все полученные по заданным категориям майноры")
    @GetMapping("/categories_sort")
    public String getSortMinorsByCategory(@RequestBody SortAllMinorsByCategoriesRequest request, Model model) {
        List<UiMinor> minors = minorService.findSortedAllMinorsByCategoryIds(request.getCategoryIds(), Sort.valueOf(request.getComparator()).getComparator());
        model.addAttribute("minors", minors);
        return "minors";
    }
}
