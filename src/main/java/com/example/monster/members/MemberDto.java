package com.example.monster.members;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {


    private Integer id;


    @NotBlank(message = "아이디는 필수값입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    private Set<MemberRole> roles;
}

