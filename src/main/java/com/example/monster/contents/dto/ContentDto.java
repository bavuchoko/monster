package com.example.monster.contents.dto;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.Replies;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

    private static ModelMapper modelMapper = new ModelMapper();

    private String category;
    @NotEmpty
    private String title;
    @NotEmpty
    private String body;
    private LocalDateTime writeTime;



    private LocalDateTime updateTime;
    @Min(0)
    private int hitCout;

    private List<Replies> replies;

    public Content toEntity() {
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper.map(this, Content.class);
    }

}
