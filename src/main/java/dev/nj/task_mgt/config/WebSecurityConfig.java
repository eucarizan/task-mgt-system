package dev.nj.task_mgt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(AccessTokenAuthenticationProvider provider, UserDetailsService userDetailsService) {
        this.accessTokenAuthenticationProvider = provider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);
        AuthenticationManager manager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        return http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/token").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tasks").authenticated()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/shutdown").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                )
                .authenticationProvider(accessTokenAuthenticationProvider)
                .addFilterAfter(new AccessTokenFilter(manager), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessions ->
                        sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(List.of(daoProvider, accessTokenAuthenticationProvider));
    }

}
