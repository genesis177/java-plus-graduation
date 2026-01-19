package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.KafkaConsumerService;
import ru.practicum.kafka.KafkaProducerService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationStarter {
    final KafkaConsumerService consumer;
    final KafkaProducerService producer;
    final UserActionService userActionService;

    @Value("${kafka.action-topic}")
    String actionTopic;

    @Value("${kafka.similarity-topic}")
    String similarityTopic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", actionTopic);
            consumer.subscribe(List.of(actionTopic));

            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                        UserActionAvro action = (UserActionAvro) record.value();
                        userActionService.updateSimilarity(action)
                                .forEach(similarity -> producer.send(similarity, similarityTopic));
                    }
                    log.info("Фиксация смещения");
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от пользователей", e);
        } finally {
            try {
                producer.flush();
                consumer.commitAsync();
            } catch (Exception e) {
                log.error("Ошибка во время сброса данных", e);
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }
}