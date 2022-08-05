package com.example.monster.contents.dto;

import com.example.monster.account.dto.AccountDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RepliesDto {

    private long id;
    private String body;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;
    private AccountDto accountDto;

}
