package pt.ulusofona.cd.store;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pt.ulusofona.cd.store.events.EventEnvelope;
import pt.ulusofona.cd.store.events.ReservationEventPayload;
import pt.ulusofona.cd.store.repository.NotificationRepository;
import pt.ulusofona.cd.store.repository.ProcessedEventRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = {
        "server.port=0",
        "spring.application.name=notification-service-test",

        // placeholders usados nas @KafkaListener e @Value(...)
        "CONSUMER_GROUP_ID=notification-service-group-test",
        "SENDER_SYSTEM=notification-service-test",
        "KAFKA_TOPIC_RESERVATION_CREATED=reservation.created",
        "KAFKA_TOPIC_RESERVATION_CONFIRMED=reservation.confirmed",
        "KAFKA_TOPIC_RESERVATION_CANCELLED=reservation.cancelled",
        "KAFKA_TOPIC_RESTAURANT_NOTIFIED=restaurant.notified",

        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class NotificationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        r.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);
        r.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        r.add("spring.kafka.admin.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    ProcessedEventRepository processedEventRepository;

    @BeforeAll
    void createTopics() throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());

        try (AdminClient admin = AdminClient.create(props)) {
            List<NewTopic> topics = List.of(
                    new NewTopic("reservation.created", 1, (short) 1),
                    new NewTopic("restaurant.notified", 1, (short) 1)
            );
            admin.createTopics(topics).all().get(20, TimeUnit.SECONDS);
        }
    }

    @Test
    void consumingReservationCreated_createsNotification_andProducesRestaurantNotified() throws Exception {
        long before = notificationRepository.count();

        ReservationEventPayload payload = new ReservationEventPayload(
                100L,
                200L,
                "Cliente Teste",
                "cliente@teste.com",
                2,
                Instant.now()
        );

        EventEnvelope<ReservationEventPayload> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                "reservation.created",
                Instant.now(),
                "reservation-service",
                payload
        );

        kafkaTemplate.send("reservation.created", envelope).get(10, TimeUnit.SECONDS);

        // Espera simples (poll) até a app processar
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            if (notificationRepository.count() == before + 1
                    && processedEventRepository.existsByReservationIdAndEventType(100L, "reservation.created")) {
                break;
            }
            Thread.sleep(200);
        }

        Assertions.assertEquals(before + 1, notificationRepository.count());
        Assertions.assertTrue(processedEventRepository.existsByReservationIdAndEventType(100L, "reservation.created"));

        // Verifica que saiu um evento em restaurant.notified (lê como String)
        Properties cprops = new Properties();
        cprops.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        cprops.put(ConsumerConfig.GROUP_ID_CONFIG, "test-verifier");
        cprops.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        cprops.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cprops.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cprops)) {
            consumer.subscribe(List.of("restaurant.notified"));

            boolean gotMessage = false;
            long end = System.currentTimeMillis() + 10_000;

            while (System.currentTimeMillis() < end && !gotMessage) {
                ConsumerRecords<String, String> records = consumer.poll(java.time.Duration.ofMillis(500));
                for (ConsumerRecord<String, String> rec : records) {
                    if (rec.value() != null && rec.value().contains("restaurant.notified")) {
                        gotMessage = true;
                        break;
                    }
                }
            }

            Assertions.assertTrue(gotMessage, "Não foi produzido nenhum evento em restaurant.notified");
        }
    }
}
