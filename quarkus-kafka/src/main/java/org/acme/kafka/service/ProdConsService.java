package org.acme.kafka.service;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.acme.kafka.util.KafkaHeaderUtil;
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

        BigInteger targetPartition = KafkaHeaderUtil.getReplyTargetPartition(kafkaRebalancedConsumerRebalanceListener.getTopicPartitions());
        String id = KafkaHeaderUtil.getGeneratedId();
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
    }

    @Incoming("request-in")
    @Outgoing("response")
    @Traced
    public Uni<Message<String>> doResponse(Message<String> message) {
        String out = message.getPayload() + LocalDateTime.now().toString();
        Optional<Integer> targetPartition = KafkaHeaderUtil.getTargetPartition(message.getMetadata(IncomingKafkaRecordMetadata.class).get());
        Optional<String> id = KafkaHeaderUtil.getHeaderId(message.getMetadata(IncomingKafkaRecordMetadata.class).get());
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



    @Incoming("response-in")
    @Traced
    public CompletionStage<Void> respond(Message<String> message) {
        log.info("Incoming Response Payload: {}", message.getPayload());
        log.info("Incoming Response ID: {}", KafkaHeaderUtil.getHeaderId(message.getMetadata(IncomingKafkaRecordMetadata.class).get()).get());
        processor.onNext(message);
        return message.ack();
    }
    

    public Uni<String> process(String message) {
        String id = sendMessage(message);

        return Uni.createFrom().multi(processor.filter(m ->
                    KafkaHeaderUtil.equalId(id, KafkaHeaderUtil.getHeaderId(m.getMetadata(IncomingKafkaRecordMetadata.class).get())))
                ) // to check, if this filter really works
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(5000)).fail()
                .onFailure(TimeoutException.class).recoverWithItem("we got a timeout")
                .onItem().invoke(m -> log.info("Responding message {}", m));
    }

}
