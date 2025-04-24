package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.SubscriptionRequestDTO;
import com.openclassrooms.mddapi.dto.SubscriptionResponseDTO;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionMapper {
    public Subscription toEntity(SubscriptionRequestDTO dto, User user , Topic topic){
        return Subscription.builder()
                .user(user)
                .topic(topic)
                .build();
    }

    public SubscriptionResponseDTO toDTO(Subscription subscription){
        return SubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getId())
                .topicId(subscription.getTopic().getId())
                .topicName(subscription.getTopic().getTitle())
                .userId(subscription.getUser().getId())
                .build();
    }
}
