package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.SubscriptionRequestDTO;
import com.openclassrooms.mddapi.dto.SubscriptionResponseDTO;
import com.openclassrooms.mddapi.mapper.SubscriptionMapper;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class for managing user subscriptions to topics.
 * Handles subscribing, unsubscribing, and fetching user subscriptions.
 */
@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AuthService securityService;
    private final TopicRepository topicRepository;
    private final SubscriptionMapper subscriptionMapper;

    /**
     * Subscribes the current user to a given topic.
     *
     * @param dto the subscription request containing the topic ID
     * @return a response DTO representing the created subscription
     * @throws ServiceException if the topic is not found, already subscribed, or saving fails
     */
    public SubscriptionResponseDTO subscribe(SubscriptionRequestDTO dto){
        try {
            User user = securityService.getCurrentUser();
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new ServiceException("Topic not found"));

            boolean alreadySubscribed = subscriptionRepository.existsByUserAndTopic(user, topic);
            if (alreadySubscribed) {
                throw new ServiceException("Already subscribed to this topic");
            }

            Subscription subscriptionEntity = subscriptionMapper.toEntity(dto, user, topic);
            subscriptionRepository.save(subscriptionEntity);

            return subscriptionMapper.toDTO(subscriptionEntity);
        } catch (Exception e) {
            throw new ServiceException("Error subscribing", e);
        }
    }

    /**
     * Unsubscribes the current user from a given topic.
     *
     * @param dto the subscription request containing the topic ID
     * @return a message confirming unsubscription
     * @throws ServiceException if the topic or subscription is not found
     */
    public Map<String, String> unsubscribe(SubscriptionRequestDTO dto){
        try {
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new ServiceException("Topic not found"));
            User user = securityService.getCurrentUser();
            Subscription subscription = subscriptionRepository.findByUserAndTopic(user, topic)
                    .orElseThrow(() -> new ServiceException("Subscription not found"));
            subscriptionRepository.delete(subscription);
            return Map.of("message", "Unsubscribed successfully");
        } catch (Exception e) {
            throw new ServiceException("Error unsubscribing", e);
        }
    }

    /**
     * Retrieves the IDs of all topics the current user is subscribed to.
     *
     * @return a list of topic IDs
     */
    public List<Long> getCurrentUserSubscribedTopicIds(){
        User user = securityService.getCurrentUser();
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
        return subscriptions.stream()
                .map(subscription -> subscription.getTopic().getId())
                .toList();
    }
}