package com.example.monster.contents.entity;


import com.example.monster.accounts.entity.Account;
import com.example.monster.common.serializers.AccountSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(columnDefinition = "Text")
    private String body;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writeTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @JsonSerialize(using = AccountSerializer.class)
    private Account account;


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="content_id", referencedColumnName = "content_id"),
            @JoinColumn(name="category", referencedColumnName = "category")
    })
    private Content content;

    public void contentSetter(Content content) {
        if(this.content != null){
            this.content.getReplies().remove(this);
        }
        this.content =content;
        content.getReplies().add(this);
    }
}
