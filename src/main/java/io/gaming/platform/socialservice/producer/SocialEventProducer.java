package io.gaming.platform.socialservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaming.platform.socialservice.model.SocialEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SocialEventProducer {
    private static final Logger log = LoggerFactory.getLogger(SocialEventProducer.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public SocialEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    /**
     * Sends a friend request event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendFriendRequestEvent(Long requesterId, Long targetId) {
        return sendSocialEvent(SocialEvent.friendRequest(requesterId, targetId));
    }

    /**
     * Sends a friend acceptance event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendFriendAcceptanceEvent(Long accepterId, Long requesterId) {
        return sendSocialEvent(SocialEvent.friendAccepted(accepterId, requesterId));
    }

    /**
     * Sends a new follower event to Kafka.
     */
    public CompletableFuture<SendResult<String, String>> sendNewFollowerEvent(Long followerId, Long followedId) {
        return sendSocialEvent(SocialEvent.newFollower(followerId, followedId));
    }

    /**
     * Generic method to send any social event to Kafka.
     */
    private CompletableFuture<SendResult<String, String>> sendSocialEvent(SocialEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            String key = event.playerId().toString();

            return kafkaTemplate.send(topicName, key, message)
                .thenApply(result -> {
                    log.info("Successfully sent social event {}: {} to topic {} partition {} offset {}",
                        event.eventType(), message, result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Failed to send social event {}: {}", event.eventType(), ex.getMessage(), ex);
                    throw new RuntimeException("Failed to send social event", ex);
                });
        } catch (Exception e) {
            log.error("Error preparing social event {}: {}", event.eventType(), e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
} 