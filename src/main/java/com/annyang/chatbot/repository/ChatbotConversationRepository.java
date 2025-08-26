package com.annyang.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annyang.chatbot.entity.ChatbotConversation;

public interface ChatbotConversationRepository extends JpaRepository<ChatbotConversation, String> {
    
}