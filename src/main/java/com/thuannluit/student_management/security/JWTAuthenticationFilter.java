package com.thuannluit.student_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = parseToken(request);
            if (token != null && !token.isEmpty()) {
                // Validate the token and set the authentication in the security context
                String username = jwtUtils.getUsernameFromToken(token);
                if (username != null) {
                    logger.info("Username from JWT: {}", username);
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtils.validateJwtToken(token)) {
                        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        // Set the details of the authentication object
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.info("Setting authentication for user: {}", username);
                        logger.info("Roles from JWT: {}", userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }

        }catch (Exception e) {
            // If there's an error parsing the token, log it and continue
            logger.error("JWT Token parsing failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);

    }

    private String parseToken(HttpServletRequest request) {
        String token =  jwtUtils.getJwtTokenFromHeader(request);
        logger.info("JWT Token: {}", token);
        return token;
    }
}
