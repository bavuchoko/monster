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
    private String category;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String body;
    private String bodyHtml;
    private String thumbnail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writeTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private int hitCout;
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="member_id")
    @JsonSerialize(using = AccountSerializer.class)
    private Account account;

    public void orMemeber(Account account) {
        this.account = account;
    }

    public void categorySetter(String category) {
        this.category=category;
    }
    @OneToMany(cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumns(value = {
        @JoinColumn(name = "content_id", updatable = false, insertable = false),
        @JoinColumn(name = "category", updatable = false, insertable = false)
    })
    private List<Replies> replies = new ArrayList<>();

    private boolean isVisible = true;

//    @Transient
//    private String thumbnail;

}
