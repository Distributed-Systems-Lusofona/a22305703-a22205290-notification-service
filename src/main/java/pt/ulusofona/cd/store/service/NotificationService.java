package pt.ulusofona.cd.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.cd.store.events.EventEnvelope;
import pt.ulusofona.cd.store.events.ReservationEventPayload;
import pt.ulusofona.cd.store.events.RestaurantNotifiedProducer;
import pt.ulusofona.cd.store.model.Notification;
import pt.ulusofona.cd.store.model.NotificationStatus;
import pt.ulusofona.cd.store.model.ProcessedEvent;
import pt.ulusofona.cd.store.repository.NotificationRepository;
import pt.ulusofona.cd.store.repository.ProcessedEventRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final RestaurantNotifiedProducer producer;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processReservationEnvelope(EventEnvelope<?> rawEnvelope) {

        String eventType = rawEnvelope.getEventType();
        String traceId = rawEnvelope.getTraceId();

        ReservationEventPayload payload =
                objectMapper.convertValue(rawEnvelope.getPayload(), ReservationEventPayload.class);

        Long reservationId = payload.getReservationId();
        if (reservationId == null) {
            throw new IllegalArgumentException("reservationId is required in payload");
        }

        Long restaurantId = payload.getRestaurantId();
        if (restaurantId == null) {
            throw new IllegalArgumentException("restaurantId is required in payload");
        }

        if (processedEventRepository.existsByReservationIdAndEventType(reservationId, eventType)) {
            return;
        }

        String recipient = payload.getCustomerEmail();

        NotificationStatus status = NotificationStatus.SENT;
        String errorMessage = null;

        try {
            System.out.println(
                    "[notification-service] Sending " + eventType +
                            " to " + recipient +
                            " for reservation " + reservationId +
                            " traceId=" + traceId
            );
        } catch (Exception ex) {
            status = NotificationStatus.FAILED;
            errorMessage = ex.getMessage();
        }

        Notification n = new Notification();
        n.setReservationId(reservationId);
        n.setRestaurantId(restaurantId);
        n.setEventType(eventType);
        n.setRecipient(recipient);
        n.setStatus(status);
        n.setSentAt(Instant.now());
        n.setErrorMessage(errorMessage);

        Notification saved = notificationRepository.save(n);

        if (status == NotificationStatus.SENT) {
            ProcessedEvent pe = new ProcessedEvent();
            pe.setReservationId(reservationId);
            pe.setEventType(eventType);
            pe.setProcessedAt(Instant.now());
            processedEventRepository.save(pe);

            producer.publishRestaurantNotified(saved, eventType, traceId);
        }
    }
}
