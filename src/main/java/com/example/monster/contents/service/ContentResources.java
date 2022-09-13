package com.example.monster.contents.service;

import com.example.monster.accounts.entity.Account;
import com.example.monster.common.serializers.AccountSerializer;
import com.example.monster.contents.ContentController;
import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.Replies;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Relation(value = "Content", collectionRelation = "replies")

public class ContentResources extends EntityModel<Content>{
    @Getter
    private long id;
    @Getter
    private String category;
    @Getter
    private String title;
    @Getter
    private String body;
    @Getter
    private String bodyHtml;
    @Getter
    private String thumbnail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writeTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    @Getter
    private int hitCout;
    @JsonSerialize(using = AccountSerializer.class)
    private Account account;
    @Getter
    private CollectionModel<EntityModel<RepliesResources>> replies;

    public ContentResources(Content content, Account account) {
        this.id =content.getId();
        this.category =content.getCategory();
        this.title =content.getTitle();
        this.body =content.getBody();
        this.bodyHtml =content.getBodyHtml();
        this.thumbnail =content.getThumbnail();
        this.writeTime =content.getWriteTime();
        this.updateTime =content.getUpdateTime();
        this.hitCout =content.getHitCout();
        this.account =content.getAccount();
        this.replies = getRepliesModel(content,account);
    }

    private CollectionModel<EntityModel<RepliesResources>> getRepliesModel(Content content, Account account) {
        return CollectionModel.of(content.getReplies().stream().map(e->{
            WebMvcLinkBuilder uri = linkTo(ContentController.class).slash(content.getCategory()).slash(e.getId());
            return e.getAccount().equals(account) ? EntityModel.of(new RepliesResources(e)).add(uri.withRel("update-reply")).add(uri.withRel("delete-reply")) : EntityModel.of(new RepliesResources(e));
        }).collect(Collectors.toList()));
    }

}
