package com.example.monster.common.security;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseBody {
    private String success;
    private String token;
    private String username;
    private String message;
}