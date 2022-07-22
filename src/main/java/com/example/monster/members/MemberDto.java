package com.example.monster.members;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {


    private Integer id;

    private String username;

    private String password;

    private Set<MemberRole> roles;
}

