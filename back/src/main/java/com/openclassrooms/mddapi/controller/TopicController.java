package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.TopicRequestDTO;
import com.openclassrooms.mddapi.dto.TopicResponseDTO;
import com.openclassrooms.mddapi.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics(){
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTopic(@RequestBody TopicRequestDTO dto){
       TopicResponseDTO topicResponseDTO = topicService.createTopic(dto);
         return ResponseEntity.ok(topicResponseDTO);
    }
}
