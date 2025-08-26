package com.annyang.chatbot.exception;

import com.annyang.global.exception.BusinessException;
import com.annyang.global.response.ErrorCode;

public class ChatbotSessionNotFoundException extends BusinessException {
    public ChatbotSessionNotFoundException() {
        super(ErrorCode.CHATBOT_SESSION_NOT_FOUND);
    }
}