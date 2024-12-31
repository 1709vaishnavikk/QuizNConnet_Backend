package com.vaishnavi.QuizNConnect.chatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepo chatRoomRepo;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIdIfNotExist) {
        return chatRoomRepo.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatID)
                .or(() -> {
                    if (createIdIfNotExist) {
                        String chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    @Transactional
    public String createChatId(String senderId, String recipientId) {
        String chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipientChatRoom = ChatRoom.builder()
                .chatID(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSenderChatRoom = ChatRoom.builder()
                .chatID(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepo.save(senderRecipientChatRoom);
        chatRoomRepo.save(recipientSenderChatRoom);

        return chatId;
    }
}
