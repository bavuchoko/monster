package com.example.monster.contents.dto;

import com.example.monster.accounts.dto.AccountDto;
import com.example.monster.contents.entity.Replies;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RepliesDto {

    private static ModelMapper modelMapper = new ModelMapper();

    private long id;
    private String body;
    private LocalDateTime writeTime;
    private LocalDateTime updateTime;
    private AccountDto accountDto;

    public Replies toEntity() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Replies.class);
    }
}
