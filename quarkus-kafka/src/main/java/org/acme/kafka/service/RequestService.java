package org.acme.kafka.service;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Tracer;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.acme.kafka.util.KafkaHeaderUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.CompletionStage;


@Slf4j
@ApplicationScoped
public class RequestService {

    @ConfigProperty(name = "reply-timeout", defaultValue = "5000")
    Integer replyTimeout;

    // a BroadcastProcessor to be able to publish for many subscribers
    private BroadcastProcessor<Message<String>> responseProcessor = BroadcastProcessor.create();

    @Inject
    KafkaRebalanceListener kafkaRebalanceListener;

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    @Inject
    Tracer tracer;

    public String sendMessage(String test) {
        String traceId  = KafkaHeaderUtil.getUberTraceId((JaegerSpanContext) tracer.activeSpan().context());
        BigInteger targetPartition = KafkaHeaderUtil.getReplyTargetPartition(kafkaRebalanceListener.getTopicPartitions());
        OutgoingKafkaRecordMetadata<String> metadataCustom = KafkaHeaderUtil.genRequestOutgoingKafkaRecordMetadata(targetPartition, traceId);
        
        Metadata metadata = Metadata.of(metadataCustom);
        Message<String> message = Message.of(test, metadata);

        emitter.send(message);
        return traceId;
    }

    @Incoming("response-in")
    @Traced
    public CompletionStage<Void> respond(Message<String> message) {
        log.info("Incoming Response Payload: {}", message.getPayload());
        log.info("Incoming Response ID: {}", KafkaHeaderUtil.getHeaderId(message.getMetadata(IncomingKafkaRecordMetadata.class).get()).get());
        responseProcessor.onNext(message);

        return message.ack();
    }


    //@KafkaReply(outgoing = "request", incoming = "response-in")
    public Uni<String> process(String message) {
        String id = sendMessage(message);

        return Uni.createFrom().multi(responseProcessor.filter(m ->
                    KafkaHeaderUtil.equalId(id, KafkaHeaderUtil.getHeaderId(m.getMetadata(IncomingKafkaRecordMetadata.class).get())))
                )
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .ifNoItem().after(Duration.ofMillis(replyTimeout)).fail()
                .onFailure(TimeoutException.class).recoverWithItem("we got a timeout")
                .onItem().invoke(m -> log.info("Responding message {}", m));
    }

    public String process2(String message) {
        String id = sendMessage(message);
        return getResponse(id)
                .await().atMost(Duration.ofMillis(replyTimeout));
    }

    private Uni<String> getResponse(String id) {
        return Uni.createFrom().multi(responseProcessor.filter(m ->
                KafkaHeaderUtil.equalId(id, KafkaHeaderUtil.getHeaderId(m.getMetadata(IncomingKafkaRecordMetadata.class).get())))
                )
                .onItem().transform(Message::getPayload)
                .onItem().invoke(t -> log.info("Received in Reactive Stream: {}", t));

    }

}
