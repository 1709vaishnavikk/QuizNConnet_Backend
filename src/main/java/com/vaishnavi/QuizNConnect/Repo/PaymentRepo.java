package com.vaishnavi.QuizNConnect.Repo;

import com.vaishnavi.QuizNConnect.Entity.Payment;
import com.vaishnavi.QuizNConnect.Entity.Question;
import com.vaishnavi.QuizNConnect.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

List<Payment> findByUser(User user); // Find payments by User object
    List<Payment> findByQuestion(Question question); // Find payments by Question object
    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByUserAndQuestion(User user, Question question); // Find payment by User and Quiz
}


