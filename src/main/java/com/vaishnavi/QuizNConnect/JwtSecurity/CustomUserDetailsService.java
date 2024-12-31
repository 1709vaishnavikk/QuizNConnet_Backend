package com.vaishnavi.QuizNConnect.JwtSecurity;

import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.Entity.UserPrinciples;
import com.vaishnavi.QuizNConnect.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserPrinciples(user);
    }
}

