package com.example.monster.accounts.dto;

import com.example.monster.accounts.entity.AccountRole;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {


    private Integer id;

    @NotBlank(message = "아이디는 필수값입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    private String nickname;
    private Set<AccountRole> roles;

    private String token;
}
