package com.annyang.member.dto;

import com.annyang.member.entity.Member;

import java.time.LocalDateTime;

public record MemberResponse(
        String id,
        String email,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
} 