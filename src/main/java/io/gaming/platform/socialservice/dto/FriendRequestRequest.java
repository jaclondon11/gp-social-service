package io.gaming.platform.socialservice.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestRequest(
    @NotNull(message = "Requester ID is required")
    Long requesterId,
    
    @NotNull(message = "Target ID is required")
    Long targetId
) {} 