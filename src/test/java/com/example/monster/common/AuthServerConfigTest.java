package com.example.monster.common;

import com.example.monster.members.Member;
import com.example.monster.members.MemberRole;
import com.example.monster.members.MemberService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest{

    @Autowired
    MemberService memberService;

    @Test
    @TestDescription("인증토큰 발급테스트")
    public void getAuthToekn() throws Exception {
        String username = "test@mail.com";
        String password = "test";
        Member member = Member.builder()
                .email(username)
                .password(password)
                .roles(Set.of(MemberRole.ADMIN, MemberRole.USER))
                .build();

        this.memberService.saveMember(member);

        String clientId = "myApp";
        String clientSecret ="pass";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId,clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type","password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("access_token").exists());
    }
}