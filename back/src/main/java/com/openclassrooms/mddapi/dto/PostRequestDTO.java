package com.openclassrooms.mddapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PostRequestDTO {
    private String title;
    private String content;
    private Long topicId;
}
