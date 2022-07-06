package com.example.monster.member;

import com.example.monster.common.AppProperties;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @Test
    public void findByUsername() {

        //Given
        Member member = Member.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(MemberRole.ADMIN,MemberRole.USER))
                .build();

        this.memberService.saveMember(member);

        //When
        UserDetailsService userDetailsService =memberService;
        UserDetails jonsgu = userDetailsService.loadUserByUsername(appProperties.getUserUsername());
        //Then
        assertThat(passwordEncoder.matches(appProperties.getUserPassword(), jonsgu.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() {
        String username = "reandom@eamil.com";

        try {
            memberService.loadUserByUsername(username);
            fail("test fail");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username);
        }


    }

}