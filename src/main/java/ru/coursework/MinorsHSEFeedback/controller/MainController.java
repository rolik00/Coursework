package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.coursework.MinorsHSEFeedback.db.Minor;
import ru.coursework.MinorsHSEFeedback.db.UiMinor;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;
import ru.coursework.MinorsHSEFeedback.service.MinorService;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private MinorService minorService;
    @Autowired
    private UiMinorMapper uiMinorMapper;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("listUsers", users);

        return "users";
    }

    @Operation(summary = "Получить все майноры")
    @GetMapping("/minors")
    public String getMinors(Model model) {
        List<Minor> minors = minorService.findAll();
        List<UiMinor> uiMinors = new ArrayList<>();
        for (Minor minor : minors) {
            uiMinors.add(uiMinorMapper.apply(minor));
        }
        model.addAttribute("minors", uiMinors);
        return "minors";
    }
}
