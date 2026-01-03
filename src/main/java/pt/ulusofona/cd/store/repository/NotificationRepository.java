package pt.ulusofona.cd.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulusofona.cd.store.model.Notification;
import pt.ulusofona.cd.store.model.NotificationStatus;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReservationId(Long reservationId);
    List<Notification> findByEventType(String eventType);
    List<Notification> findByStatus(NotificationStatus status);
    boolean existsByReservationIdAndEventType(Long reservationId, String eventType);
}
