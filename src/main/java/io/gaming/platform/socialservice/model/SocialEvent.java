package io.gaming.platform.socialservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SocialEvent(
    EventCategory category,
    SocialEventType eventType,
    String eventId,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp,
    Long playerId,
    Map<String, Object> eventData
) {
    public static SocialEvent friendRequest(Long requesterId, Long targetId) {
        return new SocialEvent(
            EventCategory.SOCIAL,
            SocialEventType.FRIEND_REQUEST,
            UUID.randomUUID().toString(),
            Instant.now(),
            requesterId,
            Map.of("targetId", targetId)
        );
    }

    public static SocialEvent friendAccepted(Long accepterId, Long requesterId) {
        return new SocialEvent(
            EventCategory.SOCIAL,
            SocialEventType.FRIEND_ACCEPTED,
            UUID.randomUUID().toString(),
            Instant.now(),
            accepterId,
            Map.of("requesterId", requesterId)
        );
    }

    public static SocialEvent newFollower(Long followerId, Long followedId) {
        return new SocialEvent(
            EventCategory.SOCIAL,
            SocialEventType.NEW_FOLLOWER,
            UUID.randomUUID().toString(),
            Instant.now(),
            followerId,
            Map.of("followedId", followedId)
        );
    }
} 