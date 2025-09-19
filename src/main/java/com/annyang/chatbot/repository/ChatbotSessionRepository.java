package com.annyang.chatbot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annyang.chatbot.entity.ChatbotSession;

public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, String> {
    List<ChatbotSession> findAllByMemberIdOrderByCreatedAtDesc(String memberId);
    boolean existsByIdAndMemberId(String sessionId, String memberId);
    Optional<ChatbotSession> findByIdAndMemberId(String sessionId, String memberId);
}
