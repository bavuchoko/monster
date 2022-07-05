package com.example.monster.member;

import com.example.monster.members.Member;
import com.example.monster.members.MemberRepository;
import com.example.monster.members.MemberRole;
import com.example.monster.members.MemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void findByUsername() {

        //Given
        String password = "pjs";
        String email = "jonsgu@email.com";
        Member member = Member.builder()
                .email(email)
                .password(password)
                .roles(Set.of(MemberRole.ADMIN,MemberRole.USER))
                .build();

        memberRepository.save(member);

        //When
        UserDetailsService userDetailsService =memberService;
        UserDetails jonsgu = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(jonsgu.getPassword()).isEqualTo(password);
    }

}