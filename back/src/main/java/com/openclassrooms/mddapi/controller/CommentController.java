package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.CommentRequestDTO;
import com.openclassrooms.mddapi.dto.CommentResponseDTO;
import com.openclassrooms.mddapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto){
        CommentResponseDTO commentResponseDTO = commentService.createComment(dto);
        return ResponseEntity.ok(commentResponseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(@PathVariable Long id){
        return ResponseEntity.ok(commentService.getAllComments(id));
    }
}
