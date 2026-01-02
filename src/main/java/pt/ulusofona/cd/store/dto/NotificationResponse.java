package pt.ulusofona.cd.store.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class NotificationResponse {
    private Long id;
    private Long reservationId;
    private String eventType;
    private String recipient;
    private String status;
    private Instant sentAt;
    private String errorMessage;
}
