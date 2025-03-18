package com.openclassrooms.mddapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SubscriptionResponseDTO {
    private Long subscriptionId;
    private Long topicId;
    private Long userId;
    private String topicName;
}
