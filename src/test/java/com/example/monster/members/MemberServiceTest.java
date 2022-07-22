package com.example.monster.members;

import com.example.monster.common.AppProperties;
import com.example.monster.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


public class MemberServiceTest extends BaseControllerTest {

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
                .username(appProperties.getUserUsername())
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