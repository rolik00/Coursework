package ru.coursework.MinorsHSEFeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.util.List;

@Controller
public class MainController {

    /*@Autowired
    private UserRepository userRepository;*/

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        //List<User> listUsers = userRepository.findAll();
        List<User> listUsers = userService.findAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }
}
