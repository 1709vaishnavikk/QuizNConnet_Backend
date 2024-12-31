package com.vaishnavi.QuizNConnect.chatMessages;

import com.vaishnavi.QuizNConnect.chatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepo chatMessageRepo;
    private final ChatRoomService chatRoomService;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        String chatId = chatRoomService.getChatId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true
        ).orElseThrow(() -> new IllegalStateException("Chat ID could not be generated"));

        chatMessage.setChatId(chatId);
        return chatMessageRepo.save(chatMessage);
    }

    public List<ChatMessage> findChatMessage(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        return chatId.map(chatMessageRepo::findByChatId).orElseGet(ArrayList::new);
    }
    public List<ChatMessage> getMessagesForUser(String userId) {
        return chatMessageRepo.findByRecipientId(userId);
    }
}
