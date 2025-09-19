package com.annyang.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "사용자 이메일", example = "test@mail.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "사용자 비밀번호", example = "a123456789!")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
} 