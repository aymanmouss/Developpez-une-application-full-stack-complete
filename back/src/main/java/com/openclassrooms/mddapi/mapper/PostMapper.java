package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.PostRequestDTO;
import com.openclassrooms.mddapi.dto.PostResponseDTO;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostMapper {

    public Post toEntity(PostRequestDTO dto, User user, Topic topic){
        return Post.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .topic(topic)
                .build();
    }
    public PostResponseDTO toDto(Post post){
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .authorId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .topicName(post.getTopic().getTitle())
                .topicId(post.getTopic().getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
