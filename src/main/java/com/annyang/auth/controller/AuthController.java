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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            String token = tokenProvider.createToken(authentication.getName(), roles);
            
            // JWT 토큰을 쿠키에 설정
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // HTTPS에서만 전송
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24시간
            cookie.setDomain("localhost");
            response.addCookie(cookie);
            
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "로그인 성공");
            
            return ResponseEntity.ok(ApiResponse.success(responseBody));
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // JWT 쿠키 삭제
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    Cookie newCookie = new Cookie("jwt", null);
                    newCookie.setHttpOnly(true);
                    newCookie.setSecure(true);
                    newCookie.setPath("/");
                    newCookie.setMaxAge(0); // 쿠키 즉시 만료
                    newCookie.setDomain("localhost");
                    response.addCookie(newCookie);
                    break;
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        
        Member member = memberRepository.findById(id)
            .orElseThrow(UnauthorizedException::new);
            
        return ResponseEntity.ok(ApiResponse.success(new MeResponse(member.getEmail(), member.getName())));
    }
} 