package io.conduktor.demos.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallBack {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallBack.class.getSimpleName());

    public static void main(String[] args) {
        // System.out.println("Hello world!");
        log.info("Hello I am a Kafka Producer!");

        // create Producer Properties
        Properties properties = new Properties();

        // connect to localhost
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092");

        // set producer properties
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer",StringSerializer.class.getName());

        properties.setProperty("batch.size", "400");

        // properties.setProperty("partitioner.class", RoundRobinPartitioner.class.getName());
        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int j=0; j<10; j++) {
            for (int i = 0; i < 30; i++) {

                // create a Producer Record
                ProducerRecord<String, String> producerRecord =
                        new ProducerRecord<>("demo_java", "hello world" + i);

                // send data
                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        // executed everytime when a record successfully sent or an exception is thrown
                        if (e == null) {
                            log.info("Received new metadata \n" +
                                    "Topic: " + metadata.topic() + "\n" +
                                    "Partition: " + metadata.partition() + "\n" +
                                    "Offset: " + metadata.offset() + "\n" +
                                    "Timestamp: " + metadata.timestamp());
                        } else {
                            log.error("Error while producing", e);
                        }
                    }
                });
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        // tell the producer to send all data and block until done -- synchronous
        producer.flush();

        // flush and close producer
        producer.close();
    }
}
