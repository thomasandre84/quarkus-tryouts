package org.acme.kafka;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletionStage;


@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);
    static final String CUSTOM = "custom";
    static final String PARTITION = "partition";
    static final String ID = "id";

    BroadcastProcessor<Message<String>> processor = BroadcastProcessor.create();

    @Inject
    KafkaRebalancedConsumerRebalanceListener kafkaRebalancedConsumerRebalanceListener;

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    public String sendMessage(String test) {
        log.info("Outgoing: {}", test);
        log.info("Listening to the following Partitions: {}", kafkaRebalancedConsumerRebalanceListener.getTopicPartitions());
        int partitionSize = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().size();
        int partitionIndex = new Random().nextInt(partitionSize);
        TopicPartition target = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().get(partitionIndex); // for tests choose a fix partition
        BigInteger targetPartition = BigInteger.valueOf(target.partition());
        log.info("Identified TargetPartition: {}", targetPartition);
        String id = UUID.randomUUID().toString();
        log.info("Generated UUID: {}", id);
        OutgoingKafkaRecordMetadata<String> metadataCustom = OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(CUSTOM)
                .withHeaders(new RecordHeaders()
                        .add(PARTITION, targetPartition.toByteArray())
                        .add(ID, id.getBytes())
                )
                .build();


        Metadata metadata = Metadata.of(metadataCustom);
        Message<String> message = Message.of(test, metadata);

        emitter.send(message);
        return id;
        /*        () -> {
                    // Called when the message is acknowledged.
                    return CompletableFuture.completedFuture(null);
                }));
        return Uni.createFrom().item(test);*/
    }

    @Incoming("request-in")
    @Outgoing("response")
    @Traced
    public Uni<Message<String>> doResponse(Message<String> message) {
        String out = message.getPayload() + LocalDateTime.now().toString();
        //message.getMetadata().forEach(m -> log.info("Metadata in Message: {}", m));
        //Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        //metadata.get().getHeaders().forEach(header -> log.info("header: key: {}, Value: {}", header.key(), new BigInteger(header.value())));
        Optional<Integer> targetPartition = getTargetPartition(message.getMetadata(IncomingKafkaRecordMetadata.class));
        Optional<String> id = getId(message.getMetadata(IncomingKafkaRecordMetadata.class));
        // Also extract UUID and add it again to metadata
        OutgoingKafkaRecordMetadata<String> metadataCustom = OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(CUSTOM)
                .withHeaders(new RecordHeaders()
                        .add(ID, id.get().getBytes())
                )
                .withPartition(targetPartition.get())
                .build();

        Metadata metaOut = Metadata.of(metadataCustom);
        log.info("Modified Outgoing " +
                "Response: {}", out);
        Message m2 = Message.of(out, metaOut);
        message.ack();
        return Uni.createFrom().item(m2);
    }

    private Optional<Integer> getTargetPartition(Optional<IncomingKafkaRecordMetadata> metadata) {

        if (metadata.isPresent()) {
            Headers headers = metadata.get().getHeaders();
            for (Header header : headers)
                if (PARTITION.equals(header.key())) {
                    BigInteger bi = new BigInteger(header.value());
                    Integer targetPartition = bi.intValue();
                    log.info("Target Partition: {}", targetPartition);
                    return Optional.of(targetPartition);
                }
        }
        return Optional.empty();
    }

    private Optional<String> getId(Optional<IncomingKafkaRecordMetadata> metadata) {

        if (metadata.isPresent()) {
            Headers headers = metadata.get().getHeaders();
            for (Header header : headers)
                if (ID.equals(header.key())) {
                    String id = new String(header.value());
                    log.info("Got UUID of: {}", id);
                    return Optional.of(id);
                }
        }
        return Optional.empty();
    }

    @Incoming("response-in")
    @Traced
    public CompletionStage<Void> respond(Message<String> message) {
        log.info("Incoming Response Payload: {}", message.getPayload());
        log.info("Incoming Response ID: {}", getId(message.getMetadata(IncomingKafkaRecordMetadata.class)).get());
        processor.onNext(message);
        return message.ack();
    }

    private boolean equalId(String initId, Optional<String> receivedId) {
        String recId = receivedId.get();
        log.info("Init Id {}  with received Id: {}", initId, recId);
        return initId.equals(recId);
    }

    public Uni<String> process(String message) {
        String id = sendMessage(message);

        return Uni.createFrom().multi(processor.filter(m -> equalId(id, getId(m.getMetadata(IncomingKafkaRecordMetadata.class))))) // to check, if this filter really works
                //.onItem().invoke(() -> sendMessage(message))
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(5000)).fail()
                .onFailure(TimeoutException.class).recoverWithItem("we got a timeout")
                .onItem().invoke(m -> log.info("Responding message {}", m));
    }

}
