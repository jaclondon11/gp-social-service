package io.gaming.platform.socialservice.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class SocialEventProducerTest {
    private static final String TOPIC = "social-events";
    private static final Long PLAYER_ID = 123L;
    private static final Long OTHER_PLAYER_ID = 456L;
    private static final String ERROR_MESSAGE = "Error serializing event";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private SocialEventProducer socialEventProducer;

    @BeforeEach
    void setUp() {
        socialEventProducer = new SocialEventProducer(kafkaTemplate, objectMapper, TOPIC);
    }

    @Test
    void sendFriendRequestEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String eventJson = """
            {"eventType":"FRIEND_REQUEST","playerId":123,"data":{"targetId":456}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = socialEventProducer.sendFriendRequestEvent(PLAYER_ID, OTHER_PLAYER_ID);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendFriendAcceptanceEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String eventJson = """
            {"eventType":"FRIEND_ACCEPTED","playerId":123,"data":{"requesterId":456}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = socialEventProducer.sendFriendAcceptanceEvent(PLAYER_ID, OTHER_PLAYER_ID);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @Test
    void sendNewFollowerEvent_ShouldSendCorrectEvent() throws Exception {
        // Arrange
        String eventJson = """
            {"eventType":"NEW_FOLLOWER","playerId":123,"data":{"followedId":456}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.completedFuture(createSendResult()));

        // Act
        CompletableFuture<SendResult<String, String>> result = socialEventProducer.sendNewFollowerEvent(PLAYER_ID, OTHER_PLAYER_ID);

        // Assert
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson));
    }

    @SuppressWarnings("serial")
    @Test
    void sendSocialEvent_ShouldHandleSerializationError() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(ERROR_MESSAGE) {});

        // Act
        CompletableFuture<SendResult<String, String>> result = socialEventProducer.sendFriendRequestEvent(PLAYER_ID, OTHER_PLAYER_ID);

        // Assert
        assertThat(result).isCompletedExceptionally();
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void sendSocialEvent_ShouldHandleKafkaError() throws JsonProcessingException {
        // Arrange
        String eventJson = """
            {"eventType":"FRIEND_REQUEST","playerId":123,"data":{"targetId":456}}""";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(eventJson);
        when(kafkaTemplate.send(eq(TOPIC), eq(PLAYER_ID.toString()), eq(eventJson)))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        // Act
        CompletableFuture<SendResult<String, String>> result = socialEventProducer.sendFriendRequestEvent(PLAYER_ID, OTHER_PLAYER_ID);

        // Assert
        assertThat(result).isCompletedExceptionally();
    }

    private SendResult<String, String> createSendResult() {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, PLAYER_ID.toString(), "test-value");
        TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
        RecordMetadata metadata = new RecordMetadata(topicPartition, 0, 0, 0, 0, 0);
        return new SendResult<>(record, metadata);
    }
} 