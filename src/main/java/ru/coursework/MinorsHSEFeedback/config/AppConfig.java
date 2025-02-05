package ru.coursework.MinorsHSEFeedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.coursework.MinorsHSEFeedback.mapper.Impl.UiMinorMapperImpl;
import ru.coursework.MinorsHSEFeedback.mapper.UiMinorMapper;

@Configuration
public class AppConfig {
    @Bean
    public UiMinorMapper uiMinorMapper() {
        return new UiMinorMapperImpl();
    }
}
