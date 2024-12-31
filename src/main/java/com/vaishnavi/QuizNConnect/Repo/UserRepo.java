package com.vaishnavi.QuizNConnect.Repo;

import com.vaishnavi.QuizNConnect.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);  // Corrected method name

    List<User> findByRole(User.Role role);  // Corrected method name

    List<User> findByStatus(User.Status status);
}
