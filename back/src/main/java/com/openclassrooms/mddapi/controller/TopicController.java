package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.TopicRequestDTO;
import com.openclassrooms.mddapi.dto.TopicResponseDTO;
import com.openclassrooms.mddapi.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling topic-related operations such as
 * retrieving all topics, creating a new topic, and fetching
 * topics followed by the current user.
 */
@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * Retrieves all available topics.
     *
     * @return a list of all topics
     */
    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics(){
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    /**
     * Creates a new topic.
     *
     * @param dto the topic data (e.g. name, description)
     * @return the created topic with its details
     */
    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTopic(@RequestBody TopicRequestDTO dto){
        TopicResponseDTO topicResponseDTO = topicService.createTopic(dto);
        return ResponseEntity.ok(topicResponseDTO);
    }

    /**
     * Retrieves the list of topics the current user is subscribed to.
     *
     * @return a list of topics associated with the current user
     */
    @GetMapping("/TopicByUserId")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByUserId(){
        List<TopicResponseDTO> topicsByUserId = topicService.getTopicsByUserId();
        return ResponseEntity.ok(topicsByUserId);
    }
}