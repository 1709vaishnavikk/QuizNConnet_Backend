package com.vaishnavi.QuizNConnect.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepo extends JpaRepository<ChatRoom, Long> {

     Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
