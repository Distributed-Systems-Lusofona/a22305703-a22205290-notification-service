package pt.ulusofona.cd.store.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantNotifiedPayload {

    @JsonProperty("notificationId")
    private Long notificationId;

    @JsonProperty("reservationId")
    private Long reservationId;

    @JsonProperty("relatedEventType")
    private String relatedEventType;

    @JsonProperty("recipient")
    private String recipient;
}
