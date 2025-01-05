package ru.coursework.MinorsHSEFeedback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.repository.UserRepository;

import java.util.regex.Pattern;

@Controller
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$");

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user, Model model) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			model.addAttribute("error", "Пользователь с такой почтой уже зарегистрирован!");
			return "error";
		}

		if (!user.getEmail().endsWith("@edu.hse.ru")) {
			model.addAttribute("error", "Не является корпоративной почтой ВШЭ");
			return "error";
		}

		if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
			model.addAttribute("error", "Password must be at least 12 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
			return "error";
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		userRepository.save(user);
		
		return "register_success";
	}

}
