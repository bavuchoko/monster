package com.example.monster.contents;

import com.example.monster.accounts.entity.Account;
import com.example.monster.accounts.entity.AccountRole;
import com.example.monster.accounts.service.AccountService;
import com.example.monster.common.BaseControllerTest;
import com.example.monster.config.AppProperties;
import com.example.monster.config.security.jwt.TokenManagerImpl;
import com.example.monster.contents.dto.ContentDto;
import com.example.monster.contents.entity.Content;
import com.example.monster.contents.repository.ContentJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
    @Autowired
    ContentJpaRepository contentJpaRepository;



    @Test
    @Description("리스트 조회 테스트")
    public void quertTest()throws Exception{

        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateContent(i);
        });


        mockMvc.perform(get("/api/content/{category}","JAVA")
                        .param("page", "1")          //페이지 0 부터 시작 -> 1은 두번째 페이지
                        .param("size", "8")
                        .param("sort", "writeTime,DESC")
                         )
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("page").exists())
                        .andExpect(jsonPath("_links.profile").exists())
                        .andDo(document("query-content",
                                preprocessRequest(
                                        Preprocessors.modifyUris()
                                                .scheme("https")
                                                .host("pjs.or.kr")
                                                .port(8080)

                                ),
                                links(
                                    linkWithRel("profile").description("프로필"),
                                    linkWithRel("first").description("첫페이지 링크"),
                                    linkWithRel("prev").description("이전페이지 링크"),
                                    linkWithRel("self").description("현재페이지 링크"),
                                    linkWithRel("next").description("다음페이지 링크"),
                                    linkWithRel("last").description("마지막페이지 링크")
                                ),
                                relaxedResponseFields(
                                        //                        responseFields(
                                    fieldWithPath("_embedded.contentList[].id").description("게시글의 식별자"),
                                    fieldWithPath("_embedded.contentList[].title").description("게시글의 제목"),
                                    fieldWithPath("_embedded.contentList[].body").description("HTML 형태의 본문"),
                                    fieldWithPath("_embedded.contentList[].bodyHtml").description("이지웍 형태의 본문 150자 > 미리보기"),
                                    fieldWithPath("_embedded.contentList[].thumbnail").description("썸네일이미지 이름"),
                                    fieldWithPath("_embedded.contentList[].writeTime").description("작성일자"),
                                    fieldWithPath("_embedded.contentList[].updateTime").description("수정일자"),
                                    fieldWithPath("_embedded.contentList[].account.nickname").description("사용자 닉네임"),
                                    fieldWithPath("_embedded.contentList[]._links.query-content.href").description("리스트조회 링크"),
                                    fieldWithPath("_embedded.contentList[]._links.self.href").description("단건 조회 링크")

                                )
                        ));
    }



    @Test
    @Description("새 게시글 등록 테스트")
    public void createTest() throws Exception {
        ContentDto content = ContentDto.builder()
                .title("제목")
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();
        mockMvc.perform(post("/api/content/{category}", "java")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken(2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(content)))
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("id").exists())
                        .andDo(document("create-content",
                                preprocessRequest(
                                        Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("pjs.or.kr")
                                        .port(8080)

                                ),
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
                                    fieldWithPath("body").description("markdown 태그 형태의 게시글 본문"),
                                    fieldWithPath("bodyHtml").description("이지웍 형태의 게시글 본문 > 미리보기"),
                                    fieldWithPath("writeTime").description("게시글 작성 일자")
                                ),
                                responseHeaders(
                                    headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON TYPE")
                                ),
                                relaxedResponseFields(
        //                          responseFields(
                                    fieldWithPath("id").description("게시글의 식별자"),
                                    fieldWithPath("title").description("게시글의 제목"),
                                    fieldWithPath("body").description("HTML 형태의 본문"),
                                    fieldWithPath("bodyHtml").description("이지웍 형태의 본문 150자 > 미리보기"),
                                    fieldWithPath("writeTime").description("작성일자"),
                                    fieldWithPath("updateTime").description("수정일자"),
                                    fieldWithPath("_links.self.href").description("자기 자신 링크"),
                                    fieldWithPath("_links.query-content.href").description("리스트 조회 링크"),
                                    fieldWithPath("_links.update-content.href").description("자신 수정 링크"),
                                    fieldWithPath("_links.profile.href").description("프로필")
                                )
                        ));
    }


    @Test
    @Description("유저정보 없이 등록 테스트")
    public void createContentWithowUser() throws Exception {
        ContentDto content = ContentDto.builder()
                .title("제목")
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();
        mockMvc.perform(post("/api/content/{category}", "java")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(content)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Description("게시글 필수내용 없이 등록테스트 400")
    public void createContentWithowNessesaryFileds() throws Exception {
        ContentDto content = ContentDto.builder()
                .title("제목")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();
        mockMvc.perform(post("/api/content/{category}", "java")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken(1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(content)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    //Todo 상세조회 테스트 200
    @Test
    @Description(" 상세조회 테스트 200")
    public void singleView() throws Exception {
        Account account = Account.builder()
                .username("admin20@email.com")
                .password("amin")
                .nickname("nick")
                .build();

        Account  user =  accountService.saveAccount(account);
        Content content = Content.builder()
                .title("제목")
                .account(user)
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        Content saved = contentJpaRepository.save(content);

        mockMvc.perform(get("/api/content/{category}/{id}", saved.getCategory(),saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @Description("적법한 수정 테스트 204")
    public void updateContent() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Account account = Account.builder()
                .username("admin22@email.com")
                .password("amin")
                .nickname("nick")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account  user =  accountService.saveAccount(account);
        String Token = this.accountService.authirize(user.getUsername(), "amin", response).getToken();
        Content content = Content.builder()
                .title("제목")
                .account(user)
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        Content saved = contentJpaRepository.save(content);

        ContentDto updateContent = ContentDto.builder()
                .title("수정된 제목")
                .body("수정된 내용")
                .category("java")
                .bodyHtml("수정된 내용")
                .updateTime(LocalDateTime.of(2022,9,05,14,30))
                .build();

        mockMvc.perform(put("/api/content/{category}/{id}", saved.getCategory(),saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+Token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateContent)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("update-content",
                        preprocessRequest(
                                Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("pjs.or.kr")
                                        .port(8080)
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Token")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("title").description("게시글의 제목"),
                                fieldWithPath("body").description("markdown 태그 형태의 게시글 본문"),
                                fieldWithPath("bodyHtml").description("이지웍 형태의 게시글 본문 > 미리보기"),
                                fieldWithPath("updateTime").description("게시글 수정 일자")
                        )
                ));
    }

    @Description("유저정보없이 수정 테스트 401")
    @Test
    public void updateWithoutUser() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Account account = Account.builder()
                .username("admin25@email.com")
                .password("amin")
                .nickname("nick")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account  user =  accountService.saveAccount(account);
        Content content = Content.builder()
                .title("제목")
                .account(user)
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        Content saved = contentJpaRepository.save(content);

        ContentDto updateContent = ContentDto.builder()
                .title("수정된 제목")
                .body("수정된 내용")
                .category("java")
                .bodyHtml("수정된 내용")
                .updateTime(LocalDateTime.of(2022,9,05,14,30))
                .build();

        mockMvc.perform(put("/api/content/{category}/{id}", saved.getCategory(),saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateContent)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Description("다른 유저 수정 테스트 401")
    @Test
    public void updateByAnotherUser() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Account account = Account.builder()
                .username("admin24@email.com")
                .password("amin")
                .nickname("nick")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account account2 = Account.builder()
                .username("admin122@email.com")
                .password("amin2")
                .nickname("nick2")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        Account  user =  accountService.saveAccount(account);
        Account  user2 =  accountService.saveAccount(account2);
        String Token = this.accountService.authirize(user2.getUsername(), "amin2", response).getToken();

        Content content = Content.builder()
                .title("제목")
                .account(user)
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        Content saved = contentJpaRepository.save(content);

        ContentDto updateContent = ContentDto.builder()
                .title("수정된 제목")
                .body("수정된 내용")
                .category("java")
                .bodyHtml("수정된 내용")
                .updateTime(LocalDateTime.of(2022,9,05,14,30))
                .build();

        mockMvc.perform(put("/api/content/{category}/{id}", saved.getCategory(),saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+Token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updateContent)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Description("적법한 삭제 테스트 204")
    @Test
    public void deleteContent() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Account account = Account.builder()
                .username("admin23@email.com")
                .password("amin")
                .nickname("nick")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        Account  user =  accountService.saveAccount(account);
        String Token = this.accountService.authirize(user.getUsername(), "amin", response).getToken();
        Content content = Content.builder()
                .title("제목")
                .account(user)
                .body("내용")
                .category("java")
                .bodyHtml("내용")
                .writeTime(LocalDateTime.of(2022,8,05,14,30))
                .build();

        Content saved = contentJpaRepository.save(content);


        mockMvc.perform(delete("/api/content/{category}/{id}", saved.getCategory(),saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+Token)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("delete-content",
                        preprocessRequest(
                                Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("pjs.or.kr")
                                        .port(8080)
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Token")
                        )
                ));
    }
    //Todo 유저정보없이 삭제 테스트 401
    //Todo 다른유저 삭제 테스트 401
    
    
    
    private String getBaererToken(int i) throws Exception {
        return "Bearer " + getAccescToken(i);
    }


    private String getAccescToken(int i) throws Exception {

        HttpServletResponse response = mock(HttpServletResponse.class);
        String username ="user"+i+"@mail.com";
        String password ="user";
        String nickname ="nick";
        Account testUser = Account.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(testUser);

        String Token = this.accountService.authirize(testUser.getUsername(), password, response).getToken();
        return Token;

    }


    private Content generateContent(int i) {
        Account account = Account.builder()
                .username("admin22"+i+"@email.com")
                .password("amin")
                .nickname("nick")
                .build();
        Account  user =  accountService.saveAccount(account);
        Content content = Content.builder()
                .title("title" + i)
                .body("test content")
                .bodyHtml("body html")
                .account(user)
                .writeTime(LocalDateTime.of(2022, 6, 17, 14, 21))
                .category("JAVA")
                .build();
        return this.contentJpaRepository.save(content);
    }

}