package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.TopicRequestDTO;
import com.openclassrooms.mddapi.dto.TopicResponseDTO;
import com.openclassrooms.mddapi.model.Topic;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicMapper {
    public Topic toEntity(TopicRequestDTO dto){
        return Topic.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }

    public TopicResponseDTO toDTO (Topic topic){
        return TopicResponseDTO.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .build();
    }
}
