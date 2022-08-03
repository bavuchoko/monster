package com.example.monster.contents;

import com.example.monster.members.Member;
import com.example.monster.members.MemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ContentDto {


    @NotEmpty
    private Category category;
    @NotEmpty
    private String title;
    @NotEmpty
    private String body;
    @NotEmpty
    private LocalDateTime writeTime;
    @NotEmpty
    private LocalDateTime updateTime;
    @Min(0)
    private int hitCout;
    @NotNull
    private MemberDto memberDto;

    private List<RepliesDto> repliesDtoList;

}
