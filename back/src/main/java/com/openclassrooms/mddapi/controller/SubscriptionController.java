package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.SubscriptionRequestDTO;
import com.openclassrooms.mddapi.dto.SubscriptionResponseDTO;
import com.openclassrooms.mddapi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> subscribe(@RequestBody SubscriptionRequestDTO dto){
        SubscriptionResponseDTO subscriptionResponseDTO = subscriptionService.subscribe(dto);
        return ResponseEntity.ok(subscriptionResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<Long>> isSubscribed(){
        List<Long> currentUserSubscribedTopicIds = subscriptionService.getCurrentUserSubscribedTopicIds();
        return ResponseEntity.ok(currentUserSubscribedTopicIds);
    }

    @DeleteMapping
    public ResponseEntity<String> unsubscribe(@RequestBody SubscriptionRequestDTO dto){
        String message = subscriptionService.unsubscribe(dto);
        return ResponseEntity.ok(message);
    }
}
