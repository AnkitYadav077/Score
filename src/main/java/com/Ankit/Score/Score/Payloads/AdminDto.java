package com.Ankit.Score.Score.Payloads;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Long parentAdminId;
}