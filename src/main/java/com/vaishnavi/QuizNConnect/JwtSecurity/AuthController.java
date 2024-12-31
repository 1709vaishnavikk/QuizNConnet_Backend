package com.vaishnavi.QuizNConnect.JwtSecurity;


import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/user/authenticate")
    public String generateToken(@RequestBody User user) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassWord()));
        } catch (Exception ex) {
            throw new Exception("Invalid username/password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
        Set<User.Role> roles = userRepo.findByUserName(user.getUserName())
                .map(User::getRole)
                .map(Collections::singleton)
                .orElse(Collections.emptySet());
//        return "User authenticated successfully with roles: " +user.getRole();
        return jwtUtil.generateToken(userDetails.getUsername(), roles);
    }

}

