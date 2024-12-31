package com.vaishnavi.QuizNConnect.JwtSecurity;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String requestURI = request.getRequestURI();
            if (requestURI.contains("/user/register") || requestURI.contains("/authenticate")|| requestURI.contains("/login")) {
                chain.doFilter(request, response);
                return;
            }

            String jwtToken = extractJwtFromRequest(request);

            if (jwtToken != null) {
                String username = jwtUtil.extractUsername(jwtToken);

                if (jwtUtil.validateToken(jwtToken, username) &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    System.out.println("Authenticated user: " + username + ", Roles: " + userDetails.getAuthorities());
                }
            }

        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during JWT processing: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }


    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null) {
            System.out.println("Authorization header is missing.");
            return null;
        }

        if (!header.startsWith("Bearer ")) {
            System.out.println("Authorization header does not start with 'Bearer'.");
            return null;
        }

        return header.substring(7); // Extract token after "Bearer "
    }
}
