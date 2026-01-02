package pt.ulusofona.cd.store.mapper;

import pt.ulusofona.cd.store.events.EventEnvelope;
import pt.ulusofona.cd.store.events.ReservationEventPayload;
import pt.ulusofona.cd.store.model.Notification;
import pt.ulusofona.cd.store.model.NotificationStatus;
import pt.ulusofona.cd.store.dto.NotificationResponse;

import java.time.Instant;

public class NotificationMapper {
    public static Notification fromReservationEvent(
            EventEnvelope<ReservationEventPayload> envelope,
            String recipient,
            NotificationStatus status,
            Instant sentAt,
            String errorMessage
    ) {
        Notification n = new Notification();

        n.setReservationId(envelope.getPayload().getReservationId());
        n.setEventType(envelope.getEventType());
        n.setRecipient(recipient);

        n.setStatus(status);
        n.setSentAt(sentAt != null ? sentAt : Instant.now());
        n.setErrorMessage(errorMessage);

        return n;
    }

    public static NotificationResponse toResponse(Notification entity) {
        NotificationResponse r = new NotificationResponse();
        r.setId(entity.getId());
        r.setReservationId(entity.getReservationId());
        r.setEventType(entity.getEventType());
        r.setRecipient(entity.getRecipient());
        r.setStatus(entity.getStatus().name());
        r.setSentAt(entity.getSentAt());
        r.setErrorMessage(entity.getErrorMessage());
        return r;
    }
}
