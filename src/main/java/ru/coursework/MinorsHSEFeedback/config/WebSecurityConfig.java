package ru.coursework.MinorsHSEFeedback.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import ru.coursework.MinorsHSEFeedback.service.CustomUserDetailsService;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
@Configuration
public class WebSecurityConfig {

	@Bean
	UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());

		http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration corsConfiguration = new CorsConfiguration();
				corsConfiguration.setAllowCredentials(true);
				corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
				corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
				corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
				corsConfiguration.setMaxAge(Duration.ofMinutes(5L));
				return corsConfiguration;
			}
		}));

		http.csrf(csrf -> csrf.disable());

		http.authorizeHttpRequests(auth ->
				auth.requestMatchers("/users").authenticated()
						.anyRequest().permitAll()
		);

		http.formLogin(login ->
				login.usernameParameter("email")
						.defaultSuccessUrl("/users")
						.permitAll()
		);

		http.logout(logout -> logout.logoutSuccessUrl("/").permitAll());

		return http.build();
	}
}