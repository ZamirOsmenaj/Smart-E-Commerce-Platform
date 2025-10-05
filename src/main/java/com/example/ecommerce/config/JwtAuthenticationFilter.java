package com.example.ecommerce.config;

import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that processes incoming HTTP requests to validate JWT tokens.
 *
 * This filter extracts the JWT from the {@code Authorization} header,
 * validates it using {@link JwtService}, and sets the authenticated
 * {@link SecurityContextHolder}
 * if the token is valid.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service for handling JWT-related operations such as extracting subjects.
     */
    private final JwtService jwtService;

    /**
     * Performs the JWT authentication filtering for each incoming request.
     *
     * Steps:
     * <ol>
     *   <li>Checks the {@code Authorization} header for a Bearer token.</li>
     *   <li>Extracts the email (subject) from the JWT using {@link JwtService}.</li>
     *   <li>If valid, creates a {@link UsernamePasswordAuthenticationToken} with the user details
     *       and sets it in the {@link SecurityContextHolder}.</li>
     *   <li>If no valid token is found, the request continues without authentication.</li>
     * </ol>
     *
     * @param request the current {@link HttpServletRequest}
     * @param response the current {@link HttpServletResponse}
     * @param filterChain the filter chain to pass the request/response to the next filter
     * @throws ServletException if an error occurs during request filtering
     * @throws IOException      if an input/output error occurs during request filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(CommonConstants.AUTH_HEADER);
        if (header == null || !header.startsWith(CommonConstants.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String email = jwtService.extractSubject(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = User
                    .withUsername(email)
                    .password(CommonConstants.EMPTY_STRING)
                    .authorities("USER")
                    .build();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
