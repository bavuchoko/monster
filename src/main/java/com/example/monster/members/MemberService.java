package com.example.monster.members;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member saveMember(Member member) {
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        return this.memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
        return new MemberAdapter(member);
    }

    public Member loginUser(String id, String password) throws Exception{
        Optional<Member> member = memberRepository.findByEmail(id);
        if(member.isEmpty()) throw new Exception ("멤버가 조회되지 않음");
        password = passwordEncoder.encode(password);
        if(!member.get().getPassword().equals(password))
            throw new Exception ("비밀번호가 틀립니다.");
        return member.get();
    }

    public Page<Member> loadUserList(Pageable pagable){
        return this.memberRepository.findAll(pagable);
    }

}
