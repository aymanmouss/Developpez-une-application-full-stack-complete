package com.openclassrooms.mddapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class TopicRequestDTO {
    private String title;
    private String description;
}
