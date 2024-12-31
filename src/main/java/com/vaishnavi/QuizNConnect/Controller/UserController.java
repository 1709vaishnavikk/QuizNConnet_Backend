package com.vaishnavi.QuizNConnect.Controller;

import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.JwtSecurity.JwtUtil;
import com.vaishnavi.QuizNConnect.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/students")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a user
    @PostMapping("/user/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("Received user: " + user);
        User registeredUser = userService.saveUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    // Login a user
    @PostMapping("/user/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.loginUser(username, password);
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(username, Set.of(User.Role.STUDENT, User.Role.ADMIN));

            return ResponseEntity.ok("Login successful!");
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    // Get a user by username
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all admins
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getUsersByRole(User.Role.ADMIN);
        return ResponseEntity.ok(admins);
    }

    // Get all students
    @GetMapping("/students/all")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userService.getUsersByRole(User.Role.STUDENT);
        return ResponseEntity.ok(students);
    }

    // Delete a user by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Disconnect a user
    @PostMapping("/disconnect")
    public ResponseEntity<User> disconnectUser(@RequestBody User user) {
        userService.disconnect(user);
        return ResponseEntity.ok(user);
    }

    // Get all connected users
    @GetMapping("/students")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
