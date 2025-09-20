package com.Ankit.Score.Score.Payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtAuthResponse {
    private String token;
    private Long adminId;
    private String email;
    private String role;
}
