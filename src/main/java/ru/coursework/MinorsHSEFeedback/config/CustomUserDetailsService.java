package ru.coursework.MinorsHSEFeedback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.coursework.MinorsHSEFeedback.db.Security;
import ru.coursework.MinorsHSEFeedback.service.SecurityService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SecurityService service;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Security security = service.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));

        return org.springframework.security.core.userdetails.User
                .withUsername(security.getLogin())
                .password(security.getPassword())
                .authorities("USER")
                .build();
    }
}
