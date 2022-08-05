package com.example.monster.account.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Account {


    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Integer id;

    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
