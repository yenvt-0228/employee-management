package com.example.employeemanagement.common.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Fails fast at startup if no Spring profile is active.
 *
 * Without this, forgetting SPRING_PROFILES_ACTIVE doesn't make the app "fail loudly" as
 * intended (see application.yml) — with no profile, no datasource.url is configured, but
 * H2 is still on the classpath (runtime scope), so Spring Boot silently auto-provisions an
 * anonymous embedded database. The app then boots "successfully" against an empty, throwaway
 * schema with no seed data and — if APP_ADMIN_USERNAME/PASSWORD were also left unset — no way
 * to log in at all, with nothing in the logs pointing at the real cause. This guard turns that
 * silent failure into an explicit startup error instead.
 */
@Configuration
public class ActiveProfileGuard implements InitializingBean {

    private final Environment environment;

    public ActiveProfileGuard(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        if (environment.getActiveProfiles().length == 0) {
            throw new IllegalStateException(
                    "No Spring profile active. Set SPRING_PROFILES_ACTIVE=dev (local) or =prod (deploy) explicitly — "
                            + "see README.md.");
        }
    }
}
