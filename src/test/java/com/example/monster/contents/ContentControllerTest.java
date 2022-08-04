package com.example.monster.contents;

import com.example.monster.common.BaseControllerTest;
import com.example.monster.common.authenticatior.AuthService;
import com.example.monster.members.Member;
import com.example.monster.members.MemberJapRepository;
import com.example.monster.members.MemberRole;
import com.example.monster.members.MemberService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ContentControllerTest extends BaseControllerTest {

    @Autowired
    ContentService contentService;

    @Autowired
    MemberService memberService;

    @Autowired
    AuthService authService;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    public void createContent() throws Exception {

        ContentDto contentDto = ContentDto.builder()
                .title("제목")
                .body("내용")
                .writeTime(LocalDateTime.of(2022,8,03,16,12,00))
                .build();

        mockMvc.perform(post("/api/content/java")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contentDto))
                        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("category").value("JAVA"))
                .andExpect(jsonPath("title").value("제목"))
                .andExpect(jsonPath("body").value("내용"));

    }

    private String getBaererToken() throws Exception {
        return "Bearer " + getAccescToken();
    }

    private String getAccescToken() throws Exception {
        //Given

        HttpServletResponse response = mock(HttpServletResponse.class);
        Member testUser = Member.builder()
                .username("test@email.com")
                .password("test")
                .roles(Set.of(MemberRole.USER))
                .build();
        String pa  = testUser.getPassword();
        this.memberService.saveMember(testUser);
        return this.authService.authirize(testUser.getUsername(), pa, response).getToken();

    }
}