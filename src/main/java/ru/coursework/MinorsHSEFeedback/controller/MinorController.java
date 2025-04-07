package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.MinorsHSEFeedback.controller.handler.GetMinorHandler;
import ru.coursework.MinorsHSEFeedback.db.ui.UiMinor;

@RestController
@RequiredArgsConstructor
public class MinorController {
    private final GetMinorHandler getMinorHandler;

    @Operation(summary = "Получить майнор по id")
    @GetMapping("/minor/{minorId}")
    public UiMinor getMinors(@PathVariable Long id) {
        return getMinorHandler.handle(id);
    }
}
