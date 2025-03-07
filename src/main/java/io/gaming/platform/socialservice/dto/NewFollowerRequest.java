package io.gaming.platform.socialservice.dto;

import jakarta.validation.constraints.NotNull;

public record NewFollowerRequest(
    @NotNull(message = "Follower ID is required")
    Long followerId,
    
    @NotNull(message = "Target ID is required")
    Long targetId
) {} 