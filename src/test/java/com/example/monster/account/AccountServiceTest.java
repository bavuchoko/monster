package com.example.monster.account;

import com.example.monster.account.entity.Account;
import com.example.monster.account.entity.AccountRole;
import com.example.monster.account.repository.AccountJapRepository;
import com.example.monster.account.service.AccountService;
import com.example.monster.common.AppProperties;
import com.example.monster.common.BaseControllerTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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


public class AccountServiceTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

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
    public void findByUsername() {

        //Given
        Account account = Account.builder()
                .username(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.accountService.saveMember(account);

        //When
        UserDetailsService userDetailsService = accountService;
        UserDetails jonsgu = userDetailsService.loadUserByUsername(appProperties.getUserUsername());
        //Then
        assertThat(passwordEncoder.matches(appProperties.getUserPassword(), jonsgu.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() {
        String username = "reandom@eamil.com";

        try {
            accountService.loadUserByUsername(username);
            fail("test fail");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username);
        }


    }

}