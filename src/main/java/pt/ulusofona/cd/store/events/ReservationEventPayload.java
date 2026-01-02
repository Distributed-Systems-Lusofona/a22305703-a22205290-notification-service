package pt.ulusofona.cd.store.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationEventPayload {

    @JsonProperty("reservationId")
    private Long reservationId;

    @JsonProperty("restaurantId")
    private Long restaurantId;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerEmail")
    private String customerEmail;

    @JsonProperty("partySize")
    private Integer partySize;

    @JsonProperty("scheduledAt")
    private Instant scheduledAt;
}
