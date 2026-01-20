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
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.handler.SimilarityHandler;
import ru.practicum.kafka.ConsumerSimService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimilarityProcessor {
    final ConsumerSimService consumer;
    final SimilarityHandler similarityHandler;

    @Value("${kafka.topics.similarity}")
    String topic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", topic + "...");
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(5000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        EventSimilarityAvro avro = (EventSimilarityAvro) record.value();
                        similarityHandler.handle(avro);
                    }
                    log.info("Фиксация смещений");
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений", e);
        } finally {
            try {
                log.info("Фиксация смещений");
                consumer.commitAsync();
            } catch (Exception e) {
                log.error("Ошибка во время сброса данных", e);
            } finally {
                consumer.close();
            }
        }
    }
}