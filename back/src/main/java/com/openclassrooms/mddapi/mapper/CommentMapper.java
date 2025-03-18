package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.CommentRequestDTO;
import com.openclassrooms.mddapi.dto.CommentResponseDTO;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentMapper {
    public Comment toEntity(CommentRequestDTO dto, Post post, User user){
        return Comment.builder()
                .content(dto.getContent())
                .post(post)
                .user(user)
                .build();
    }

    public CommentResponseDTO toDTO(Comment comment){
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .username(comment.getUser().getUsername())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
