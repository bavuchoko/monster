package com.example.monster.members;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberAdapter extends User {

    private Member member;


    public MemberAdapter(Member member) {
        super(member.getUsername(), member.getPassword(), authorities(member.getRoles()));
        this.member =member;
    }



    private static Collection<? extends GrantedAuthority> authorities(Set<MemberRole> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    public Member getMember() {
        return member;
    }
}
