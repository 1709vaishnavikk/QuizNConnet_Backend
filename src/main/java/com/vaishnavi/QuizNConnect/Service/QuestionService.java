package com.vaishnavi.QuizNConnect.Service;

import com.vaishnavi.QuizNConnect.Entity.Payment;
import com.vaishnavi.QuizNConnect.Entity.Question;
import com.vaishnavi.QuizNConnect.Entity.User;
import com.vaishnavi.QuizNConnect.Repo.PaymentRepo;
import com.vaishnavi.QuizNConnect.Repo.QuestionRepo;
import com.vaishnavi.QuizNConnect.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService implements IquestService{
    private final QuestionRepo questionRepository;
    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> getQuestionById(Long quizId) {
        return questionRepository.findById(quizId);
    }

    @Override
    public List<String> getAllSubjects() {
        return questionRepository.findDistinctSubject();
    }

    @Override
    public Question updateQuestion(Long id, Question question) throws ChangeSetPersister.NotFoundException {
        Optional<Question> theQuestion = this.getQuestionById(question.getQuizId());
        if (theQuestion.isPresent()){
            Question updatedQuestion = theQuestion.get();
            updatedQuestion.setQuestion(question.getQuestion());
            updatedQuestion.setChoices(question.getChoices());
            updatedQuestion.setCorrectAns(question.getCorrectAns());
            return questionRepository.save(updatedQuestion);
        }else {
            throw new ChangeSetPersister.NotFoundException();
        }

    }
    @Override
    public void deleteQuestion(Long quizId) {
        questionRepository.deleteById(quizId);
    }
    @Override
    public List<Question> getQuestionsForUser(Integer numOfQuestions, String subject) {
        Pageable pageable = PageRequest.of(0, numOfQuestions);
        return questionRepository.findBySubject(subject, pageable).getContent();
    }

    public boolean hasAccessToQuiz(Long userId, Long quizId) {
        // Fetch User by userId
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch Quiz by quizId
        Question quiz = questionRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Fetch payments by User and Quiz
        Optional<Payment> payments = paymentRepo.findByUserAndQuestion(user, quiz);

        // Check if there is any payment with status "PAID"
        return payments.stream().anyMatch(payment -> payment.getStatus().equals("PAID"));
    }


}
