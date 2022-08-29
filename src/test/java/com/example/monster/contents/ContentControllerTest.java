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
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
                .title("제목")
                .body("내용")
                .category("java")
                .bodyPreView("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        mockMvc.perform(post("/api/content/{category}", "java")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(content)))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("id").exists())
                        .andDo(document("create-content",
                                links(
                                        linkWithRel("self").description("자기 자신의 링크"),
                                        linkWithRel("query-content").description("리스트 조회 링크"),
                                        linkWithRel("update-content").description("수정 링크"),
                                        linkWithRel("profile").description("프로필")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Token")
                                ),
                                relaxedRequestFields(
                                        fieldWithPath("title").description("게시글의 제목"),
                                        fieldWithPath("body").description("HTML 태그 형태의 게시글 본문"),
                                        fieldWithPath("bodyPreView").description("이지웍 형태의 게시글 본문 > 미리보기"),
                                        fieldWithPath("writeTime").description("게시글 작성 일자")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON TYPE")
                                ),
                                relaxedResponseFields(
        //                        responseFields(
                                        fieldWithPath("id").description("게시글의 식별자"),
                                        fieldWithPath("title").description("게시글의 제목"),
                                        fieldWithPath("body").description("HTML 형태의 본문"),
                                        fieldWithPath("bodyPreView").description("이지웍 형태의 본문 150자 > 미리보기"),
                                        fieldWithPath("writeTime").description("작성일자"),
                                        fieldWithPath("updateTime").description("수정일자"),
                                        fieldWithPath("_links.self.href").description("자기 자신 링크"),
                                        fieldWithPath("_links.query-content.href").description("리스트 조회 링크"),
                                        fieldWithPath("_links.update-content.href").description("자신 수정 링크"),
                                        fieldWithPath("_links.profile.href").description("프로필")
                                )

                        ));


    }
    private String getBaererToken() throws Exception {
        return "Bearer " + getAccescToken();
    }


    private String getAccescToken() throws Exception {

        HttpServletResponse response = mock(HttpServletResponse.class);
        String username ="user@mail.com";
        String password ="user";
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