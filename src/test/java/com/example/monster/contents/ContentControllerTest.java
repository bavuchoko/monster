package com.example.monster.contents;

import com.example.monster.accounts.entity.Account;
import com.example.monster.accounts.entity.AccountRole;
import com.example.monster.accounts.service.AccountService;
import com.example.monster.common.BaseControllerTest;
import com.example.monster.config.AppProperties;
import com.example.monster.config.security.jwt.TokenManagerImpl;
import com.example.monster.contents.dto.ContentDto;
import com.example.monster.contents.entity.Content;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ContentControllerTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AppProperties appProperties;
    @Autowired
    TokenManagerImpl tokenManager;
    @Test
    public void createTest() throws Exception {
        ContentDto content = ContentDto.builder()
                .body("내용")
                .title("제목")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        mockMvc.perform(post("/api/content/{category}", "java")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(content)))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("id").exists());


    }
    private String getBaererToken() throws Exception {
        return "Bearer " + getAccescToken();
    }


    private String getAccescToken() throws Exception {

        HttpServletResponse response = mock(HttpServletResponse.class);
        String username ="aaa@mail.com";
        String password ="pass";
        String nickname ="nick";
        Account testUser = Account.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveMember(testUser);

        String Token = this.accountService.authirize(testUser.getUsername(), password, response).getToken();
        return Token;

    }

}