package com.example.monster.contents.entity;


import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentImage {
    @Id
    @GeneratedValue
    private Integer ID;
    @NotNull
    private Integer contentId;
    @NotEmpty
    private String origianlFileName;
    @NotEmpty
    private String filePath;

    private long size;
}
