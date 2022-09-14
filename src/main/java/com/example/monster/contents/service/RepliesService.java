package com.example.monster.contents.service;

import com.example.monster.contents.entity.Replies;
import com.example.monster.contents.repository.RepliesJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RepliesService {

    @Autowired
    RepliesJpaRepository repliesJpaRepository;

    public Replies writeReply(Replies reply) {
        return repliesJpaRepository.save(reply);
    }

}
