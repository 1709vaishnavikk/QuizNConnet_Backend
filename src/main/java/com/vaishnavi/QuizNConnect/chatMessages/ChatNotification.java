package com.vaishnavi.QuizNConnect.chatMessages;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChatNotification {
        private Long id;
        private String senderId;
        private String recipientId;
        private String content;}

