package com.vaishnavi.QuizNConnect.Controller;

import com.razorpay.Order;
import com.vaishnavi.QuizNConnect.Entity.Payment;
import com.vaishnavi.QuizNConnect.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> createOrder(@RequestParam Long userId,
                                         @RequestParam Long quizId) {
        try {
            Order order = paymentService.createOrder(userId, quizId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create payment order: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePaymentStatus(@RequestParam String orderId,
                                                 @RequestParam String paymentId,
                                                 @RequestParam String status) {
        try {
            paymentService.updatePaymentStatus(orderId, paymentId, status);
            return ResponseEntity.ok().body("Payment status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update payment status: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsForUser(userId));
    }

    @GetMapping("/user/{userId}/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getPaymentByUserAndQuiz(@PathVariable Long userId,
                                                     @PathVariable Long quizId) {
        try {
            Payment payment = paymentService.getPaymentByUserAndQuiz(userId, quizId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Payment not found: " + e.getMessage());
        }
    }
}
