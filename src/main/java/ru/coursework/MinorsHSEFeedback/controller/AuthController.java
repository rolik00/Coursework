package ru.coursework.MinorsHSEFeedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.coursework.MinorsHSEFeedback.components.EmailSender;
import ru.coursework.MinorsHSEFeedback.db.User;
import ru.coursework.MinorsHSEFeedback.db.ui.UiUser;
import ru.coursework.MinorsHSEFeedback.mapper.UiUserMapper;
import ru.coursework.MinorsHSEFeedback.request.RegistrationRequest;
import ru.coursework.MinorsHSEFeedback.request.UpdatePasswordRequest;
import ru.coursework.MinorsHSEFeedback.service.UserService;

import java.security.Principal;
import java.util.regex.Pattern;

import static ru.coursework.MinorsHSEFeedback.enums.Errors.IS_EXIST_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.IS_NOT_HSE_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.PASSWORD_MATCH_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.PASSWORD_NOT_MATCH_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.UNCORRECT_PASSWORD_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Errors.UNRELIABLE_PASSWORD_ERROR;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.BEGIN;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.END;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.REGISTRATION;
import static ru.coursework.MinorsHSEFeedback.enums.Letters.UPDATE_PASSWORD;

@Controller
@Slf4j
public class AuthController {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailSender emailSender;

	@Autowired
	private UiUserMapper uiUserMapper;

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$");

	/*@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}*/

	@Operation(summary = "Регистрация нового пользователя")
	@PostMapping("/process_register")
	public ResponseEntity<?> processRegister(@RequestBody RegistrationRequest request) {
		log.info("invoke processRegister");
		if (userService.findByEmail(request.getEmail()).isPresent()) {
			log.error("User = {}, error = {} ", request.getEmail(), IS_EXIST_ERROR.getTitle());
			return ResponseEntity.badRequest().body(IS_EXIST_ERROR.getTitle());
		}

		if (!request.getEmail().endsWith("@edu.hse.ru")) {
			log.error("User = {}, error = {} ", request.getEmail(), IS_NOT_HSE_ERROR.getTitle());
			return ResponseEntity.badRequest().body(IS_NOT_HSE_ERROR.getTitle());
		}

		if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
			log.error("User = {}, error = {} ", request.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
			return ResponseEntity.badRequest().body(UNRELIABLE_PASSWORD_ERROR.getTitle());
		}

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			log.error("User = {}, error = {} ", request.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
			return ResponseEntity.badRequest().body(PASSWORD_NOT_MATCH_ERROR.getTitle());
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setMinorTitle(request.getMinorTitle());
		user.setCourseTitle(request.getCourseTitle());
		user.setPassword(encodedPassword);
		userService.save(user);

		String content = BEGIN.getTitle() + user.getName() + REGISTRATION.getTitle() + END.getTitle();
		emailSender.sendEmail(user.getEmail(), "Регистрация на портале MinorsHSEFeedbacks", content);
		log.info("Пользователь {} успешно зарегистрирован!", user.getEmail());
		return ResponseEntity.ok(uiUserMapper.apply(user));
	}

	/*@GetMapping("/update_password")
	public String updatePasswordForm() {
		return "update_password";
	}*/

	@Operation(summary = "Обновление пароля")
	@PostMapping("/update_password")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
		log.info("invoke updatePassword");
		UiUser uiUser = null;
		try {
			User user = userService.findByEmail(request.getLogin())
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(request.getNewPassword());

			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), UNCORRECT_PASSWORD_ERROR.getTitle());
				return ResponseEntity.badRequest().body(UNCORRECT_PASSWORD_ERROR.getTitle());
			}

			if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), PASSWORD_MATCH_ERROR.getTitle());
				return ResponseEntity.badRequest().body(PASSWORD_MATCH_ERROR.getTitle());
			}

			if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
				log.error("User = {}, error = {} ", user.getEmail(), PASSWORD_NOT_MATCH_ERROR.getTitle());
				return ResponseEntity.badRequest().body(PASSWORD_NOT_MATCH_ERROR.getTitle());
			}

			if(!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
				log.error("User = {}, error = {} ", user.getEmail(), UNRELIABLE_PASSWORD_ERROR.getTitle());
				return ResponseEntity.badRequest().body(UNRELIABLE_PASSWORD_ERROR.getTitle());
			}

			user.setPassword(encodedPassword);
			userService.save(user);
			uiUser = uiUserMapper.apply(user);

			String content = BEGIN.getTitle() + user.getName() + UPDATE_PASSWORD.getTitle() + END.getTitle();
			emailSender.sendEmail(user.getEmail(), "Обновление пароля на портале MinorsHSEFeedbacks", content);
			log.info("Пользователь {} изменил пароль", user.getEmail());

		} catch (Exception e) {
			log.error("При попытке смены пароля для пользователя {} произошла ошибка {}", request.getLogin(), e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(uiUser);
	}
}
