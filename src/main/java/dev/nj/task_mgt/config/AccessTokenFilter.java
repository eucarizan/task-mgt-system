package dev.nj.task_mgt.config;

import dev.nj.task_mgt.entities.AccessTokenAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccessTokenFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> matchers = new ArrayList<>();
    private final AuthenticationManager manager;
    private final AuthenticationEntryPoint authenticationEntryPoint = ((request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(authException.getMessage());
    });

    public AccessTokenFilter(AuthenticationManager manager) {
        this.manager = manager;

        addRequestMatcher(new AntPathRequestMatcher("/api/tasks", HttpMethod.POST.name()));
        addRequestMatcher(new AntPathRequestMatcher("/api/tasks", HttpMethod.GET.name()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isMatched = matchers.stream().anyMatch(matcher -> matcher.matches(request));
        if (isMatched) {
            try {
                var token = request.getHeader("Access-Token");
                if (token != null) {
                    Authentication authentication = new AccessTokenAuthentication(token);
                    authentication = manager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
                throw new BadCredentialsException("Access token is required");
            } catch (AuthenticationException ex) {
                authenticationEntryPoint.commence(request, response, ex);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void addRequestMatcher(RequestMatcher matcher) {
        this.matchers.add(matcher);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/api/auth/token")
                && request.getMethod().equals(HttpMethod.POST.name());
    }
}
