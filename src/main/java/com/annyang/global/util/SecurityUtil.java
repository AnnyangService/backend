package com.annyang.global.util;

import com.annyang.member.entity.Member;
import com.annyang.member.repository.MemberRepository;
import com.annyang.member.exception.MemberNotFoundException;
import com.annyang.auth.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final MemberRepository memberRepository;

    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }
        
        return memberRepository.findById(authentication.getName())
                .orElseThrow(MemberNotFoundException::new);
    }
} 