package pt.ulusofona.cd.store.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pt.ulusofona.cd.store.service.NotificationService;

@Service
@RequiredArgsConstructor
public class ReservationEventsConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.reservation-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationCreated(String message, Acknowledgment ack) {
        EventEnvelope<?> envelope = parse(message);
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topics.reservation-confirmed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationConfirmed(String message, Acknowledgment ack) {
        EventEnvelope<?> envelope = parse(message);
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topics.reservation-cancelled}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationCancelled(String message, Acknowledgment ack) {
        EventEnvelope<?> envelope = parse(message);
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }

    private EventEnvelope<?> parse(String message) {
        try {
            return objectMapper.readValue(message, EventEnvelope.class);
        } catch (Exception e) {
            // Em DEV: não bloquear o consumer por causa de "poison pill"
            throw new IllegalArgumentException("Failed to parse EventEnvelope from Kafka message", e);
        }
    }
}
