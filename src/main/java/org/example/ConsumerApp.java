package org.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.avro.model.Event;
import org.example.avro.model.Student;
import org.example.avro.model.Teacher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class ConsumerApp {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:19092");
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", AvroDeserializer.class);
        properties.put("group.id", "simple-consumer-example");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "3000");
        properties.put("compression.type", "snappy");

        try (Consumer<String, Event> consumer = new KafkaConsumer<>(properties, new StringDeserializer(),new AvroDeserializer(Event.class))) {
            consumer.subscribe(Collections.singletonList("some-topic"));
            while(true) {
                final ConsumerRecords<String, Event> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
                records.forEach(r -> {
                    switch(r.value().getEntry()) {
                        case Student s-> System.out.println(s);
                        case Teacher t-> System.out.println(t);
                        default -> System.out.println("unknown");
                    }
                });
            }

        }
    }
}
