package com.example.monster.account;

import com.example.monster.account.entity.Account;
import com.example.monster.account.repository.AccountJapRepository;
import com.example.monster.common.AppProperties;
import com.example.monster.common.BaseControllerTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class AccountControllerTest extends BaseControllerTest {

    @Autowired
    AccountJapRepository accountJapRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void delete() {
        accountJapRepository.deleteAll();
    }


    @Test
    public void loginTest() throws Exception {

        Account account = Account.builder()
                .username("user2@email.com")
                .password("user")
                .build();

            mockMvc.perform(post("/user/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(account)))
                    .andDo(print())
                    .andExpect(status().isOk())
        ;
    }



}