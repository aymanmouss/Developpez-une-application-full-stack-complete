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

/**
 * Service responsible for handling business logic related to comments.
 * Supports creating comments and retrieving comments for a specific post.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;

    /**
     * Creates a new comment associated with a specific post and user.
     *
     * @param dto the comment data including post ID and content
     * @return the created comment as a response DTO
     * @throws ServiceException if the post is not found or saving fails
     */
    public CommentResponseDTO createComment(CommentRequestDTO dto){
        try {
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new ServiceException("Post not found"));

            User user = authService.getCurrentUser();

            Comment commentEntity = commentMapper.toEntity(dto, post, user);

            commentRepository.save(commentEntity);

            return commentMapper.toDTO(commentEntity);

        } catch (Exception e) {
            throw new ServiceException("Error creating comment", e);
        }
    }

    /**
     * Retrieves all comments associated with a specific post.
     *
     * @param postId the ID of the post
     * @return a list of comment response DTOs
     * @throws ServiceException if fetching the comments fails
     */
    public List<CommentResponseDTO> getAllComments(Long postId){
        try {
            List<Comment> comments = commentRepository.findAllByPostId(postId);
            return comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Error getting comments", e);
        }
    }
}