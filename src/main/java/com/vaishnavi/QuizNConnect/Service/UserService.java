package com.vaishnavi.QuizNConnect.Service;

import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Login a user
    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = userRepo.findByUserName(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassWord())) {
            return user;
        }
        return Optional.empty();
    }

    // Save or register a user
    public User saveUser(User user) {
        user.setStatus(User.Status.ONLINE);  // Default status during registration
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));  // Encode password before saving
        return userRepo.save(user);
    }

    // Find a user by username
    public Optional<User> findUserByUsername(String username) {
        return userRepo.findByUserName(username);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // Get users by role
    public List<User> getUsersByRole(User.Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return userRepo.findByRole(role);
    }

    // Disconnect a user
    public void disconnect(User user) {
        Optional<User> existingUser = userRepo.findByUserName(user.getUserName());
        if (existingUser.isPresent()) {
            existingUser.get().setStatus(User.Status.OFFLINE);
            userRepo.save(existingUser.get());
        }
    }

    // Find all connected users
    public List<User> findConnectedUsers() {
        return userRepo.findByStatus(User.Status.ONLINE);
    }

    // Delete a user
    public void deleteUser(Long userId) {
        if (userRepo.existsById(userId)) {
            userRepo.deleteById(userId);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
