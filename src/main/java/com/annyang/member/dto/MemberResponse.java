package com.annyang.member.dto;

import com.annyang.member.entity.Member;

public record MemberResponse(
        Long id,
        String email,
        String name
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
} 