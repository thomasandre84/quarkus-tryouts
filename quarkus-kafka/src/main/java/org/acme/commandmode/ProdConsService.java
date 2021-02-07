package org.acme.commandmode;

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
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);
    static final String PARTITION = "partition";

    BroadcastProcessor<Message<String>> processor = BroadcastProcessor.create();

    @Inject
    KafkaRebalancedConsumerRebalanceListener kafkaRebalancedConsumerRebalanceListener;

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    public void sendMessage(String test) {
        log.info("Outgoing: {}", test);
        log.info("Listening to the following Partitions: {}", kafkaRebalancedConsumerRebalanceListener.getTopicPartitions());
        TopicPartition target = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().get(3);
        BigInteger targetPartition = BigInteger.valueOf(target.partition());
        OutgoingKafkaRecordMetadata<String> metadataPartition = OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(PARTITION)
                .withHeaders(new RecordHeaders().add(PARTITION, targetPartition.toByteArray()))
                //.withPartition(target.partition())
                .build();

        Metadata metadata = Metadata.of(metadataPartition);
        Message<String> message = Message.of(test, metadata);
        //message.addMetadata(metadataPartition);

        emitter.send(message);
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
        Optional<OutgoingKafkaRecordMetadata<String>> metadataPartition = getTargetPartition(message.getMetadata(IncomingKafkaRecordMetadata.class));

        Metadata metaOut = Metadata.of(metadataPartition.get());
        log.info("Modified Outgoing " +
                "Response: {}", out);
        Message m2 = Message.of(out, metaOut);
        message.ack();
        return Uni.createFrom().item(m2);
    }

    private Optional<OutgoingKafkaRecordMetadata<String>> getTargetPartition(Optional<IncomingKafkaRecordMetadata> metadata) {

        if (metadata.isPresent()) {
            Headers headers = metadata.get().getHeaders();
            for (Header header : headers)
                if (PARTITION.equals(header.key())) {
                    BigInteger bi = new BigInteger(header.value());
                    Integer targetPartition = bi.intValue();
                    OutgoingKafkaRecordMetadata<String> metadataPartition = OutgoingKafkaRecordMetadata.<String>builder()
                            .withPartition(targetPartition)
                            .build();
                    return Optional.of(metadataPartition);
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
        sendMessage(message);
        return Uni.createFrom().multi(processor)
                //.onItem().invoke(() -> sendMessage(message))
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(5000)).fail()
                .onFailure(TimeoutException.class).recoverWithItem("we got a timeout")
                .onItem().invoke(m -> log.info("Responding message {}", m));
    }

}
