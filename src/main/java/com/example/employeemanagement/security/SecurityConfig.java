package com.example.employeemanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Authorization:
 *  - ADMIN: full access (create/update/delete, actuator).
 *  - USER : view list, search, statistics.
 * REST API (/api/**) uses HTTP Basic, stateless; the web UI uses form login + session.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/api/lab3/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/**").authenticated()
                        .anyRequest().hasRole("ADMIN"))
                .httpBasic(basic -> basic.authenticationEntryPoint(restAuthenticationEntryPoint))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        AntPathRequestMatcher h2Console = new AntPathRequestMatcher("/h2-console/**");
        http.authorizeHttpRequests(auth -> auth
                        .antMatchers("/hello", "/login", "/register", "/error", "/css/**", "/js/**").permitAll()
                        .antMatchers("/actuator/health/**").permitAll()
                        .requestMatchers(h2Console).hasRole("ADMIN")
                        .mvcMatchers("/employees/add", "/employees/*/edit", "/employees/*/delete").hasRole("ADMIN")
                        .antMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/employees/list", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(h2Console))
                .headers(headers -> headers.frameOptions().sameOrigin());
        return http.build();
    }
}
