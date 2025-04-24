package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentRequestDTO;
import com.openclassrooms.mddapi.dto.CommentResponseDTO;
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

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @Operation(summary = "Create a new post", security = @SecurityRequirement(name = "bearer-jwt"))
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO dto){
        return ResponseEntity.ok(postService.createPost(dto));
    }
    @Operation(summary = "Get all posts", security = @SecurityRequirement(name = "bearer-jwt"))
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts(){
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }


}
