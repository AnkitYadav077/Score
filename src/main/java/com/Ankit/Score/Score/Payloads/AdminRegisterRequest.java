package com.Ankit.Score.Score.Payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {
    private String name;
    private String email;
    private String password;
}