package org.example;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.avro.model.Event;
import org.example.avro.model.Student;
import org.example.avro.model.Teacher;

public class ProducerApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:19092");
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", AvroSerializer.class);
        properties.put("acks", "1");
        properties.put("retries", "3");
        properties.put("compression.type", "snappy");

        try (Producer<String, Event> producer = new KafkaProducer<>(properties)) {
            while(true) {
                Event studentEvent = new Event(new Student("student", "id", 10));
                Event teacherEvent = new Event(new Teacher("teacher","t20"));
                ProducerRecord<String, Event> studentRecord = new ProducerRecord<>("some-topic", "some-key", studentEvent);
                ProducerRecord<String, Event> teacherRecord = new ProducerRecord<>("some-topic", "some-key", teacherEvent);
                Future<RecordMetadata> studentSendFuture = producer.send(studentRecord);
                Future<RecordMetadata> teacherSendFuture = producer.send(teacherRecord);
                studentSendFuture.get();
                teacherSendFuture.get();
                Thread.sleep(1000);
            }
        }
    }
}
