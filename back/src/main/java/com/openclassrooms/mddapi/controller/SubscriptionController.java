package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.SubscriptionRequestDTO;
import com.openclassrooms.mddapi.dto.SubscriptionResponseDTO;
import com.openclassrooms.mddapi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing user subscriptions to topics.
 * Allows users to subscribe, check current subscriptions, and unsubscribe.
 */
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Subscribes the current user to a topic.
     *
     * @param dto the subscription request containing the topic ID
     * @return the subscription response with confirmation details
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> subscribe(@RequestBody SubscriptionRequestDTO dto){
        SubscriptionResponseDTO subscriptionResponseDTO = subscriptionService.subscribe(dto);
        return ResponseEntity.ok(subscriptionResponseDTO);
    }

    /**
     * Retrieves a list of topic IDs the current user is subscribed to.
     *
     * @return a list of topic IDs
     */
    @GetMapping
    public ResponseEntity<List<Long>> isSubscribed(){
        List<Long> currentUserSubscribedTopicIds = subscriptionService.getCurrentUserSubscribedTopicIds();
        return ResponseEntity.ok(currentUserSubscribedTopicIds);
    }

    /**
     * Unsubscribes the current user from a topic.
     *
     * @param dto the subscription request containing the topic ID
     * @return a message indicating success or failure
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestBody SubscriptionRequestDTO dto){
        Map<String, String> message = subscriptionService.unsubscribe(dto);
        return ResponseEntity.ok(message);
    }
}