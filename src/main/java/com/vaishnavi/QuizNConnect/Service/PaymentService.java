package com.vaishnavi.QuizNConnect.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.vaishnavi.QuizNConnect.Entity.Payment;
import com.vaishnavi.QuizNConnect.Entity.Question;
import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.Repo.PaymentRepo;
import com.vaishnavi.QuizNConnect.Repo.QuestionRepo;
import com.vaishnavi.QuizNConnect.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    private final PaymentRepo paymentRepo;
    private final QuestionRepo questionRepo;
    private final UserRepo userRepo;

    public Order createOrder(Long userId, Long quizId) throws Exception {
        // Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch Quiz
        Question quiz = questionRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Create Razorpay Order
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (quiz.getPrice() * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "quiz_" + quizId + "_" + userId);

        Order order = razorpayClient.orders.create(orderRequest);

        // Create Payment Entity
        Payment payment = Payment.builder()
                .orderId(order.get("id"))
                .user(user)
                .question(quiz)
                .amount(quiz.getPrice())
                .status("CREATED")
                .timestamp(LocalDateTime.now())
                .build();

        // Save Payment
        paymentRepo.save(payment);

        return order;
    }

    public void updatePaymentStatus(String orderId, String paymentId, String status) {
        // Fetch Payment by Order ID
        Payment payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Update Payment Details
        payment.setPaymentId(paymentId);
        payment.setStatus(status);

        // Save Updated Payment
        paymentRepo.save(payment);
    }

    public List<Payment> getPaymentsForUser(Long userId) {
        // Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch Payments for the User
        return paymentRepo.findByUser(user);
    }

    public Payment getPaymentByUserAndQuiz(Long userId, Long quizId) {
        // Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch Quiz
        Question quiz = questionRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Fetch Payment by User and Quiz
        return paymentRepo.findByUserAndQuestion(user, quiz)
                .orElseThrow(() -> new RuntimeException("Payment not found for the given user and quiz"));
    }
}

