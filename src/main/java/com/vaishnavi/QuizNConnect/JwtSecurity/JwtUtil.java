package com.vaishnavi.QuizNConnect.JwtSecurity;

import com.vaishnavi.QuizNConnect.Entity.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "55bd03988f2f591a5523fd42eb5311fc45a473d0c715899672a9391facb7f208";

    // Token validity in milliseconds (10 hours)
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10;

    public String generateToken(String username, Set<User.Role> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<User.Role> extractRoles(String token) {
        try {
            List<String> roles = (List<String>) getClaims(token).get("roles");
            Set<User.Role> roleSet = new HashSet<>();
            if (roles != null) {
                for (String role : roles) {
                    roleSet.add(User.Role.valueOf(role));
                }
            }
            return roleSet;
        } catch (Exception e) {
            System.out.println("Error extracting roles: " + e.getMessage());
            return new HashSet<>();
        }
    }


    public boolean validateToken(String token, String username) {
        try {
            return extractUsername(token).equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Error parsing token: " + e.getMessage());
            throw e;
        }
    }
}
