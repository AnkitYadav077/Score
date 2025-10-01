package com.Ankit.Score.Score.Payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Long parentAdminId;
}
