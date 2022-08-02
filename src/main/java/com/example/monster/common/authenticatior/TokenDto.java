package com.example.monster.common.authenticatior;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {


    private String code;
    private String token;
    private String username;
    private String message;
}