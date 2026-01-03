package pt.ulusofona.cd.store.events;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pt.ulusofona.cd.store.service.NotificationService;

@Service
@RequiredArgsConstructor
public class ReservationEventsConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topics.reservation-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationCreated(EventEnvelope<?> envelope, Acknowledgment ack) {
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topics.reservation-confirmed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationConfirmed(EventEnvelope<?> envelope, Acknowledgment ack) {
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topics.reservation-cancelled}", groupId = "${spring.kafka.consumer.group-id}")
    public void onReservationCancelled(EventEnvelope<?> envelope, Acknowledgment ack) {
        notificationService.processReservationEnvelope(envelope);
        ack.acknowledge();
    }
}
