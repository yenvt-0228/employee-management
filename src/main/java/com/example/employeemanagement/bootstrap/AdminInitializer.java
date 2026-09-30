package com.example.employeemanagement.bootstrap;

import com.example.employeemanagement.modules.user.Role;
import com.example.employeemanagement.modules.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Creates the first ADMIN account from configuration (env APP_ADMIN_USERNAME / APP_ADMIN_PASSWORD).
 * Self-registered users always get the USER role, so this is the only way to get an ADMIN account in prod.
 */
@Component
@Order(1)
public class AdminInitializer implements ApplicationRunner {

    private final UserService userService;
    private final String username;
    private final String password;

    public AdminInitializer(UserService userService,
                            @Value("${app.admin.username:}") String username,
                            @Value("${app.admin.password:}") String password) {
        this.userService = userService;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password) && !userService.exists(username)) {
            userService.createUser(username, password, "Administrator", Role.ADMIN);
        }
    }
}
