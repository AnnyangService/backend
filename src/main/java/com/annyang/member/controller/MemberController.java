package com.annyang.member.controller;

import com.annyang.global.response.ApiResponse;
import com.annyang.global.response.ErrorCode;
import com.annyang.member.dto.MemberRequest;
import com.annyang.member.dto.MemberResponse;
import com.annyang.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@RequestBody @Valid MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.created(URI.create("/members/" + response.id()))
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMember(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberService.getAllMembers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(@PathVariable Long id, @RequestBody @Valid MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.updateMember(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 