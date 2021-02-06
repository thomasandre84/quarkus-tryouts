package org.acme.commandmode;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import io.smallrye.mutiny.subscription.MultiEmitter;
import org.apache.kafka.common.TopicPartition;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);

    BroadcastProcessor<Message<String>> processor = BroadcastProcessor.create();

    @Inject
    KafkaRebalancedConsumerRebalanceListener kafkaRebalancedConsumerRebalanceListener;

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    public void sendMessage(String test) {
        log.info("Outgoing: {}", test);
        log.info("Listening to the following Partitions: {}", kafkaRebalancedConsumerRebalanceListener.getTopicPartitions());
        TopicPartition target = kafkaRebalancedConsumerRebalanceListener.getTopicPartitions().get(4);
        Metadata metadata = Metadata.of(target);
        Message<String> message = Message.of(test, metadata);

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
        message.getMetadata().forEach(m -> log.info("Metadata in Message: {}", m));
        log.info("Modified Outgoing " +
                "Response: {}", out);
        Message m2 = Message.of(out, message.getMetadata());
        message.ack();
        return Uni.createFrom().item(m2);
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
