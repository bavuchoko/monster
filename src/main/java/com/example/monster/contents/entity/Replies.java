package com.example.monster.contents.entity;


import com.example.monster.account.entity.Account;
import com.example.monster.common.serializers.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Replies implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "reply_id")
    private long id;

    private String body;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @JsonSerialize(using = AccountSerializer.class)
    private Account account;
}
