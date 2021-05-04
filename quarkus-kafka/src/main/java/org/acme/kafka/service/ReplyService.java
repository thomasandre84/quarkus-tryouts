package org.acme.kafka.service;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.acme.kafka.interceptor.ConsumerTraceBinding;
import org.acme.kafka.util.KafkaHeaderUtil;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@ApplicationScoped
public class ReplyService {
    @Inject
    @Channel("response")
    Emitter<String> responseEmitter;

    @Inject
    ManagedExecutor managedExecutor;

    @Incoming("request-in")
    @Traced
    @ConsumerTraceBinding
    public CompletionStage<Void> getRequest(Message<String> message) {
        String out = message.getPayload() + " " + LocalDateTime.now(); // The operation

        Uni<Void> uni = null;
        CompletableFuture<Void> ret = managedExecutor.runAsync(() -> {
            try {
                sendOptionalResponse(out, message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            ret.get(2000l, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            message.nack(e);
        }
        return message.ack();
    }

    private void sendOptionalResponse(String out, Message<String> message) throws InterruptedException {
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        Thread.sleep(20000L);
        if (metadata.isPresent()) {
            Optional<Integer> targetPartition = KafkaHeaderUtil.getTargetPartition(metadata.get());
            Optional<String> id = KafkaHeaderUtil.getHeaderId(metadata.get());
            if (targetPartition.isPresent() && id.isPresent()) {
                OutgoingKafkaRecordMetadata<String> metadataCustom = KafkaHeaderUtil.genResponseOutgoingKafkaRecordMetadata(targetPartition.get(), id.get());
                Metadata metaOut = Metadata.of(metadataCustom);
                log.info("Modified Outgoing Response: {}", out);
                Message<String> m2 = Message.of(out, metaOut);
                doResponse(m2);
            }
        }
    }

    public void doResponse(Message<String> message){
        responseEmitter.send(message);
    }
}
