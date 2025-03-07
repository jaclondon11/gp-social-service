package io.gaming.platform.socialservice.controller;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.gaming.platform.socialservice.dto.FriendAcceptanceRequest;
import io.gaming.platform.socialservice.dto.FriendRequestRequest;
import io.gaming.platform.socialservice.dto.NewFollowerRequest;
import io.gaming.platform.socialservice.dto.SocialEventResponse;
import io.gaming.platform.socialservice.model.SocialEvent;
import io.gaming.platform.socialservice.model.SocialEventType;
import io.gaming.platform.socialservice.producer.SocialEventProducer;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/social-events")
public class SocialEventController {
    private static final Logger log = LoggerFactory.getLogger(SocialEventController.class);
    
    private final SocialEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    public SocialEventController(SocialEventProducer eventProducer, ObjectMapper objectMapper) {
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/friend-request")
    public ResponseEntity<SocialEventResponse> sendFriendRequest(@Valid @RequestBody FriendRequestRequest request) {
        log.debug("Processing friend request event from player: {} to player: {}", 
            request.requesterId(), request.targetId());
        return handleSocialEvent(
        		() -> eventProducer.sendFriendRequestEvent(
        				request.requesterId(),
        				request.targetId()),
            SocialEventType.FRIEND_REQUEST,
            request.requesterId()
        );
    }

    @PostMapping("/friend-acceptance")
    public ResponseEntity<SocialEventResponse> sendFriendAcceptance(@Valid @RequestBody FriendAcceptanceRequest request) {
        log.debug("Processing friend acceptance event from player: {} to player: {}", 
            request.acceptorId(), request.requesterId());
        return handleSocialEvent(
        		() -> eventProducer.sendFriendAcceptanceEvent(
        				request.acceptorId(),
        				request.requesterId()),
            SocialEventType.FRIEND_ACCEPTED,
            request.acceptorId()
        );
    }

    @PostMapping("/new-follower")
    public ResponseEntity<SocialEventResponse> sendNewFollower(@Valid @RequestBody NewFollowerRequest request) {
        log.debug("Processing new follower event from player: {} to player: {}", 
            request.followerId(), request.targetId());
        return handleSocialEvent(
        		() -> eventProducer.sendNewFollowerEvent(
        				request.followerId(),
        				request.targetId()),
            SocialEventType.NEW_FOLLOWER,
            request.followerId()
        );
    }
    
    private ResponseEntity<SocialEventResponse> handleSocialEvent(
            Supplier<CompletableFuture<SendResult<String, String>>> eventSupplier,
            SocialEventType eventType,
            Long playerId) {
        try {
            SendResult<String, String> result = eventSupplier.get().join();
            log.debug("Successfully processed {} event for player: {}", eventType, playerId);
            return buildSuccessResponse(result);
        } catch (Exception ex) {
            log.error("Failed to process {} event for player: {}", eventType, playerId, ex);
            return buildErrorResponse(eventType, playerId, ex.getMessage());
        }
    }

    private ResponseEntity<SocialEventResponse> buildSuccessResponse(SendResult<String, String> result) {
        try {
            SocialEvent event = objectMapper.readValue(result.getProducerRecord().value(), SocialEvent.class);
            return ResponseEntity.accepted().body(
        		SocialEventResponse.success(
                    event.eventId(),
                    event.category(),
                    event.eventType(),
                    event.timestamp(),
                    event.playerId()
                )
            );
        } catch (Exception e) {
            log.error("Failed to deserialize event response", e);
            throw new RuntimeException("Failed to process response: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<SocialEventResponse> buildErrorResponse(
            SocialEventType eventType, Long playerId, String errorMessage) {
        return ResponseEntity.internalServerError()
            .body(SocialEventResponse.error(eventType, playerId, errorMessage));
    }
} 