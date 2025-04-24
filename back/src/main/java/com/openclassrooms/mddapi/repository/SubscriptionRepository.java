package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
