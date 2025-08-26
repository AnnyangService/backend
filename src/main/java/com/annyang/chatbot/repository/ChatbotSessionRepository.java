package com.annyang.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annyang.chatbot.entity.ChatbotSession;

public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, String> {
}
