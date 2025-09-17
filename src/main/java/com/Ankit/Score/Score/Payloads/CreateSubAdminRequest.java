package com.Ankit.Score.Score.Payloads;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubAdminRequest {
    private String name;
    private String email;
    private String password;
}