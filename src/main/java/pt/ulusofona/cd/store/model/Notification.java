package pt.ulusofona.cd.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_reservation_id", columnList = "reservation_id"),
                @Index(name = "idx_notifications_event_type", columnList = "event_type"),
                @Index(name = "idx_notifications_status", columnList = "status"),
                @Index(name = "idx_notifications_restaurant_id", columnList = "restaurant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    // NOVO
    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @NotBlank
    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @NotBlank
    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) sentAt = Instant.now();
        if (status == null) status = NotificationStatus.PENDING;
    }
}
