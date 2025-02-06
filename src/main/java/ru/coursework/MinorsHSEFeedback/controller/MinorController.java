package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;
import ru.coursework.MinorsHSEFeedback.service.MinorService;

@Controller
public class MinorController {
    @Autowired
    private MinorService minorService;

    @Operation(summary = "Получить майнор по id")
    @GetMapping("/minor/{minorId}")
    public UiMinor getMinors(@PathVariable Long id) {
        return minorService.getMinor(id);
    }
}
