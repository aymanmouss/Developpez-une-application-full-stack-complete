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

@RequiredArgsConstructor
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AuthService securityService;
    private final TopicRepository topicRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionResponseDTO subscribe(SubscriptionRequestDTO dto){
      try{
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
      }catch (Exception e){
            throw new ServiceException("Error subscribing", e);
      }
    }

    public String unsubscribe(SubscriptionRequestDTO dto){
        try{
            Subscription topic = subscriptionRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new ServiceException("Subscription not found"));
            User user = securityService.getCurrentUser();
            Subscription subscription = subscriptionRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new ServiceException("Topic not found"));
            subscriptionRepository.delete(subscription);
            return "Unsubscribed successfully";
        }catch (Exception e){
            throw new ServiceException("Error unsubscribing", e);
        }
    }

    public List<Long>getCurrentUserSubscribedTopicIds(){
        User user = securityService.getCurrentUser();
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
        return subscriptions.stream()
                .map(subscription -> subscription.getTopic().getId())
                .toList();
    }

}
