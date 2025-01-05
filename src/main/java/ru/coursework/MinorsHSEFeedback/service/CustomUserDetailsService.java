package ru.coursework.MinorsHSEFeedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.coursework.MinorsHSEFeedback.db.CustomUserDetails;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new CustomUserDetails(user);
	}

}
