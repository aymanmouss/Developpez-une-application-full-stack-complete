package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.CommentRequestDTO;
import com.openclassrooms.mddapi.dto.CommentResponseDTO;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;

    public CommentResponseDTO createComment(CommentRequestDTO dto){
        try{
            Post post  = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new ServiceException("Post not found"));

            User user = authService.getCurrentUser();

            Comment commentEntity = commentMapper.toEntity(dto, post, user);

            commentRepository.save(commentEntity);

            return commentMapper.toDTO(commentEntity);

        }catch (Exception e){
            throw new ServiceException("Error creating comment", e);
        }
    }

    public List<CommentResponseDTO> getAllComments(Long postId){
        try{
            List<Comment> comments = commentRepository.findAllByPostId(postId);
            return comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new ServiceException("Error getting comments", e);
        }

    }

}
