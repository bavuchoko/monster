package com.example.monster.contents.service;

import com.example.monster.accounts.entity.Account;
import com.example.monster.common.serializers.AccountSerializer;
import com.example.monster.contents.entity.Replies;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;

public class RepliesResources extends EntityModel<Replies> {

    @Getter
    private long id;
    @Getter
    private String body;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writeTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @JsonSerialize(using =AccountSerializer.class)
    private Account account;

    public RepliesResources(Replies reply) {
        this.id =reply.getId();
        this.body =reply.getBody();
        this.writeTime =reply.getWriteTime();
        this.updateTime =reply.getUpdateTime();
        this.account =reply.getAccount();
    }
}
