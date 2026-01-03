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
public class EventEnvelope<T> {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("occurredAt")
    private Instant occurredAt;

    @JsonProperty("traceId")
    private String traceId;

    @JsonProperty("payload")
    private T payload;
}
