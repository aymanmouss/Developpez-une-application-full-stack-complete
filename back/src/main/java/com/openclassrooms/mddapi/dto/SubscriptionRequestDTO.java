package com.openclassrooms.mddapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SubscriptionRequestDTO {
    @NotNull (message = "User ID is required")
    private Long topicId;
}
