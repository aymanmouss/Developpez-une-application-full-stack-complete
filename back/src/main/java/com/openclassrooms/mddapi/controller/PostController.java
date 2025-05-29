package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.PostRequestDTO;
import com.openclassrooms.mddapi.dto.PostResponseDTO;
import com.openclassrooms.mddapi.service.CommentService;
import com.openclassrooms.mddapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling operations related to posts,
 * including creating, retrieving all, and retrieving by ID.
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * Creates a new post.
     *
     * @param dto the post data including title, content, and theme
     * @return the created post with its metadata
     */
    @Operation(summary = "Create a new post", security = @SecurityRequirement(name = "bearer-jwt"))
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO dto){
        return ResponseEntity.ok(postService.createPost(dto));
    }

    /**
     * Retrieves all available posts.
     *
     * @return a list of all posts
     */
    @Operation(summary = "Get all posts", security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts(){
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /**
     * Retrieves a specific post by its ID.
     *
     * @param id the ID of the post to retrieve
     * @return the post with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }
}