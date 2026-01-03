package pt.ulusofona.cd.store.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ulusofona.cd.store.model.Notification;

import java.time.Instant;
import java.util.UUID;

@Service
public class RestaurantNotifiedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.restaurant-notified}")
    private String restaurantNotifiedTopic;

    public RestaurantNotifiedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRestaurantNotified(Notification notification, String relatedEventType, String traceId) {

        String tid = (traceId != null && !traceId.isBlank()) ? traceId : UUID.randomUUID().toString();

        RestaurantNotifiedPayload payload = new RestaurantNotifiedPayload(
                notification.getId(),
                notification.getReservationId(),
                notification.getRestaurantId(),
                relatedEventType,
                notification.getRecipient()
        );

        EventEnvelope<RestaurantNotifiedPayload> envelope = new EventEnvelope<>(
                "restaurant.notified",
                notification.getStatus().name(),
                Instant.now(),
                tid,
                payload
        );

        String key = String.valueOf(notification.getReservationId());
        kafkaTemplate.send(restaurantNotifiedTopic, key, envelope);
    }
}
