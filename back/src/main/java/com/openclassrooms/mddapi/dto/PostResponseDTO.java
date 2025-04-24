package com.openclassrooms.mddapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class PostResponseDTO {

    private Long id;

    private Long authorId;

    private String username;

    private String topicName;

    private Long topicId;

    private String title;

    private String content;

    private LocalDateTime createdAt;
}
