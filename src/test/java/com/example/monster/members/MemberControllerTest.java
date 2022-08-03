package com.example.monster.members;

import com.example.monster.common.AppProperties;
import com.example.monster.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class MemberControllerTest extends BaseControllerTest {

    @Autowired
    MemberJapRepository memberJapRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @Test
    public void loginTest() throws Exception {

        Member member = Member.builder()
                .username("user@email.com")
                .password("user")
                .build();

            mockMvc.perform(post("/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(member)))
                    .andDo(print())
                    .andExpect(status().isOk())
        ;
    }



}