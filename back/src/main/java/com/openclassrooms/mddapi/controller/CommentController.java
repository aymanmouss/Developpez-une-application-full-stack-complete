package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentRequestDTO;
import com.openclassrooms.mddapi.dto.CommentResponseDTO;
import com.openclassrooms.mddapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling comment-related operations such as
 * creating comments and retrieving comments for a specific post.
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Creates a new comment.
     *
     * @param dto the comment data including post ID and content
     * @return the created comment with metadata
     */
    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto){
        CommentResponseDTO commentResponseDTO = commentService.createComment(dto);
        return ResponseEntity.ok(commentResponseDTO);
    }

    /**
     * Retrieves all comments associated with a specific post.
     *
     * @param id the ID of the post
     * @return a list of comments for the given post
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(@PathVariable Long id){
        return ResponseEntity.ok(commentService.getAllComments(id));
    }
}
