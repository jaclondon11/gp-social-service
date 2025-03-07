package io.gaming.platform.socialservice.dto;

import io.gaming.platform.socialservice.model.EventCategory;
import io.gaming.platform.socialservice.model.SocialEventType;

import java.time.Instant;

public record SocialEventResponse(
    String eventId,
    EventCategory category,
    SocialEventType eventType,
    Instant timestamp,
    Long playerId,
    String errorMessage
) {
    public static SocialEventResponse success(
            String eventId,
            EventCategory category,
            SocialEventType eventType,
            Instant timestamp,
            Long playerId) {
        return new SocialEventResponse(eventId, category, eventType, timestamp, playerId, null);
    }

    public static SocialEventResponse error(
            SocialEventType eventType,
            Long playerId,
            String errorMessage) {
        return new SocialEventResponse(
            null,
            EventCategory.SOCIAL,
            eventType,
            Instant.now(),
            playerId,
            errorMessage
        );
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }
} 