package com.vaishnavi.QuizNConnect.chatMessages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatId(String chatId);

    List<ChatMessage> findByRecipientId(String recipientId);
}
