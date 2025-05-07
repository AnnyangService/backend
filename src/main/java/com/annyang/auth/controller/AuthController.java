package com.annyang.auth.controller;

import com.annyang.auth.dto.LoginRequest;
import com.annyang.auth.dto.MeResponse;
import com.annyang.auth.dto.SignUpRequest;
import com.annyang.auth.exception.UnauthorizedException;
import com.annyang.auth.token.JwtTokenProvider;
import com.annyang.global.response.ApiResponse;
import com.annyang.member.entity.Member;
import com.annyang.member.exception.EmailDuplicateException;
import com.annyang.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignUpRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailDuplicateException();
        }

        Member member = new Member(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getName()
        );

        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            String token = tokenProvider.createToken(authentication.getName(), roles);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "로그인 성공");
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(UnauthorizedException::new);
            
        return ResponseEntity.ok(ApiResponse.success(new MeResponse(member.getEmail(), member.getName())));
    }
} 