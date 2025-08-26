package com.annyang.chatbot.entity;

import com.annyang.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatbot_conversation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatbotConversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatbot_session_id", nullable = false)
    private ChatbotSession chatbotSession;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Builder
    public ChatbotConversation(ChatbotSession chatbotSession, String question, String answer) {
        this.chatbotSession = chatbotSession;
        this.question = question;
        this.answer = answer;
    }
}
