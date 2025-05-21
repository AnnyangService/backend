package com.annyang.auth.controller;

import com.annyang.auth.config.AuthConfig;
import com.annyang.auth.dto.LoginRequest;
import com.annyang.auth.dto.MeResponse;
import com.annyang.auth.dto.SignUpRequest;
import com.annyang.auth.exception.UnauthorizedException;
import com.annyang.auth.token.JwtTokenProvider;
import com.annyang.global.response.ApiResponse;
import com.annyang.member.entity.Member;
import com.annyang.member.exception.EmailDuplicateException;
import com.annyang.member.repository.MemberRepository;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    /**
     * Refresh Token을 쿠키에 설정하는 메서드
     * @param refreshToken Refresh Token
     * @return ResponseCookie
     */
    private ResponseCookie createRefreshTokenCookie(final String refreshToken) {
        return ResponseCookie.from(AuthConfig.RefreshToken.NAME, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path(AuthConfig.RefreshToken.PATH)
            .maxAge(AuthConfig.RefreshToken.MAX_AGE)
            .build();
    }

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
            
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            
            String accessToken = tokenProvider.createToken(authentication.getName(), roles);
            String refreshToken = tokenProvider.createRefreshToken(authentication.getName(), roles);
            
            ResponseCookie cookie = createRefreshTokenCookie(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            responseBody.put("message", "로그인 성공");
            
            return ResponseEntity.ok(ApiResponse.success(responseBody));
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            @Parameter(hidden = true) @CookieValue(name = AuthConfig.RefreshToken.NAME) String refreshToken,
            HttpServletResponse response) {
        
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException();
        }
        
        String memberId = tokenProvider.getMemberId(refreshToken);
        List<String> roles = tokenProvider.getRoles(refreshToken);
        
        String newAccessToken = tokenProvider.createToken(memberId, roles);
        String newRefreshToken = tokenProvider.createRefreshToken(memberId, roles);
        
        ResponseCookie cookie = createRefreshTokenCookie(newRefreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", newAccessToken);
        
        return ResponseEntity.ok(ApiResponse.success(responseBody));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(AuthConfig.RefreshToken.NAME, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path(AuthConfig.RefreshToken.PATH)
            .maxAge(0)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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