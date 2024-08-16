package dev.nj.task_mgt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())                           // enable basic HTTP authentication
                .authorizeHttpRequests(auth -> auth                             // other matchers
                        .requestMatchers("/error").permitAll()                  // expose /error endpoint
                        .requestMatchers("/actuator/shutdown").permitAll()      // required for tests
                        .requestMatchers("/h2-console/**").permitAll())         // expose h2 console
                .csrf(AbstractHttpConfigurer::disable)                          // allow modifying request from tests
                .sessionManagement(sessions ->
                        sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no session
                .build();
    }
}
