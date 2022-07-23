package com.example.monster.common;

import com.example.monster.common.AppProperties;
import com.example.monster.members.Member;
import com.example.monster.members.MemberRole;
import com.example.monster.members.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            MemberService memberService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
//                Member admin = Member.builder()
//                        .email(appProperties.getAdminUsername())
//                        .password(appProperties.getAdminPassword())
//                        .roles(Set.of(MemberRole.ADMIN, MemberRole.USER))
//                        .build();
//                memberService.saveMember(admin);
                Member user = Member.builder()
                        .username(appProperties.getUserUsername())
                        .password("user")
                        .roles(Set.of(MemberRole.USER))
                        .build();
                memberService.saveMember(user);
            }
        };
    }
}
