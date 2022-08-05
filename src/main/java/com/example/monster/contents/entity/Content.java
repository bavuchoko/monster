package com.example.monster.contents.entity;


import com.example.monster.accounts.entity.Account;
import com.example.monster.common.serializers.AccountSerializer;
import com.example.monster.contents.Category;
import com.example.monster.contents.ContentId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@IdClass(ContentId.class)
@Entity
public class Content {

    @Id @GeneratedValue
    @Column(name = "content_id")
    private long id;

    @Id
    @Enumerated(EnumType.STRING)
    private Category category;

    private String title;
    private String body;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;
    private int hitCout;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="member_id")
    @JsonSerialize(using = AccountSerializer.class)
    private Account account;

    public void orMemeber(Account account) {
        this.account = account;
    }
    public void orCategory(Category category) {
        this.category =category;
    }

    @OneToMany(cascade=CascadeType.PERSIST)
    @JoinColumns(value = {
        @JoinColumn(name = "content_id", updatable = false, insertable = false),
        @JoinColumn(name = "category", updatable = false, insertable = false)
    })
    private List<Replies> replies = new ArrayList<>();

}
