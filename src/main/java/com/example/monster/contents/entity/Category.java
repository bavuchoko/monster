package com.example.monster.contents.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String header;

    @Column(unique = true)
    private String category;

}
