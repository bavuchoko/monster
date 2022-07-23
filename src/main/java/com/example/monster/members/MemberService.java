package com.example.monster.members;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;


    private final PasswordEncoder passwordEncoder;



    public Member saveMember(Member member) {
        System.out.println(member.getPassword());
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        String aa = this.passwordEncoder.encode("user");
        return this.memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new MemberAdapter(member);
    }


    public Member loginUser(String username, String password) throws UsernameNotFoundException, BadCredentialsException {
        Optional<Member> member = memberRepository.findByUsername(username);
        if(member.isEmpty()){
            throw new UsernameNotFoundException("username does not exist : 존재하지 않는 아이디 입니다.");
        }
        Member queriedMember = member.get();
        if(member.isPresent() && !this.passwordEncoder.matches(password, queriedMember.getPassword()) ){
            throw new BadCredentialsException("password do not match : 비밀번호가 일치하지 않습니다.");
        }
        if(member.isPresent() && this.passwordEncoder.matches(password, queriedMember.getPassword())) {
            return member.get();
        }
        return null;
    }

    public Page<Member> loadUserList(Pageable pagable){
        return this.memberRepository.findAll(pagable);
    }

}
