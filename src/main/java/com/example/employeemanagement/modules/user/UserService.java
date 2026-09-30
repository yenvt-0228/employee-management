package com.example.employeemanagement.modules.user;

import com.example.employeemanagement.common.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Lab 2: PasswordEncoder is a bean defined in AppConfig, injected via the constructor.
    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** A self-registered user always gets the USER role. */
    @Transactional
    public AppUser register(RegisterRequest request) {
        return createUser(request.getUsername().trim(), request.getPassword(), request.getFullName(), Role.USER);
    }

    @Transactional
    public AppUser createUser(String username, String rawPassword, String fullName, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("username", "Tên đăng nhập '" + username + "' đã tồn tại");
        }
        AppUser user = userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), fullName, role));
        log.info("Created user '{}' with role {}", username, role);
        return user;
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }
}
