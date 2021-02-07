package org.acme.commandmode;

import io.smallrye.mutiny.Multi;
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

    public UUID sendMessage(String test) {
        log.info("Outgoing: {}", test);
        log.info("Listening to the following Partitions: {}", kafkaRebalancedConsumerRebalanceListener.getTopicPartitions());
        int partitionSize = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().size();
        int partitionIndex = new Random().nextInt(partitionSize);
        TopicPartition target = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().get(partitionIndex); // for tests choose a fix partition
        BigInteger targetPartition = BigInteger.valueOf(target.partition());
        log.info("Identified TargetPartition: {}", targetPartition);
        UUID id = UUID.randomUUID();
        OutgoingKafkaRecordMetadata<String> metadataCustom = OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(CUSTOM)
                .withHeaders(new RecordHeaders()
                        .add(PARTITION, targetPartition.toByteArray())
                        .add(ID, id.toString().getBytes())
                )
                .build();


        Metadata metadata = Metadata.of(metadataCustom);
        Message<String> message = Message.of(test, metadata);
        //message.addMetadata(metadataPartition);

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
        Optional<UUID> id = getId(message.getMetadata(IncomingKafkaRecordMetadata.class));
        // Also extract UUID and add it again to metadata
        OutgoingKafkaRecordMetadata<String> metadataCustom = OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(CUSTOM)
                .withHeaders(new RecordHeaders()
                        .add(ID, id.get().toString().getBytes())
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

    private Optional<UUID> getId(Optional<IncomingKafkaRecordMetadata> metadata) {

        if (metadata.isPresent()) {
            Headers headers = metadata.get().getHeaders();
            for (Header header : headers)
                if (ID.equals(header.key())) {
                    UUID id = UUID.nameUUIDFromBytes(header.value());
                    log.info("Got UUID of: {}", id);
                    return Optional.of(id);
                }
        }
        return Optional.empty();
    }

    @Incoming("response-in")
    @Traced
    public CompletionStage<Void> respond(Message<String> message) {
        log.info("Incoming Response: {}", message.getPayload());
        processor.onNext(message);
        return message.ack();
    }

    public Uni<String> process(String message) {
        UUID id = sendMessage(message);
        /*Multi<Message<String>> multi = processor;
        multi.filter(m -> id.equals(getId(m.getMetadata(IncomingKafkaRecordMetadata.class)).get()))
                .onItem().invoke(t -> log.info("Received Filteres in Reactive Stream: {}", t))
                .subscribe().with(
                        item -> log.info("Item in Mulit: {}", item),
                        f -> log.warn("Failed: ", f)
                );*/

        return Uni.createFrom().multi(processor) // to check, if this filter really works
                //.onItem().invoke(() -> sendMessage(message))
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(5000)).fail()
                .onFailure(TimeoutException.class).recoverWithItem("we got a timeout")
                .onItem().invoke(m -> log.info("Responding message {}", m));
    }

}
