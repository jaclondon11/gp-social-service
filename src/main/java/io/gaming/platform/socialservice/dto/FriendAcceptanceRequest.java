package io.gaming.platform.socialservice.dto;

import jakarta.validation.constraints.NotNull;

public record FriendAcceptanceRequest(
    @NotNull(message = "Acceptor ID is required")
    Long acceptorId,
    
    @NotNull(message = "Requester ID is required")
    Long requesterId
) {} 