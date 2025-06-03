package com.annyang.member.service;

import com.annyang.member.dto.MemberRequest;
import com.annyang.member.dto.MemberResponse;
import com.annyang.member.entity.Member;
import com.annyang.member.exception.EmailDuplicateException;
import com.annyang.member.exception.MemberNotFoundException;
import com.annyang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailDuplicateException();
        }

        Member member = new Member(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name()
        );

        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(String id) {
        return memberRepository.findById(id)
                .map(MemberResponse::from)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    public MemberResponse updateMember(String id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        if (!member.getEmail().equals(request.email()) &&
                memberRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailDuplicateException();
        }

        member.update(request.email(), request.password(), request.name());
        return MemberResponse.from(memberRepository.save(member));
    }

    public void deleteMember(String id) {
        if (!memberRepository.existsById(id)) {
            throw new MemberNotFoundException();
        }
        memberRepository.deleteById(id);
    }
} 