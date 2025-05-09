package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserAndTopic(User user, Topic topic);
    Optional<Subscription> findByUserAndTopic(User user, Topic topic);
    List<Subscription> findByUser(User user);
    Long findByTopic(Topic topic);
}
