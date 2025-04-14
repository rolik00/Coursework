package ru.coursework.MinorsHSEFeedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;
import ru.coursework.MinorsHSEFeedback.service.CustomUserDetailsService;

@Configuration
public class UserDetailConfig {
    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

}
