package org.acme.kafka.service;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.acme.kafka.interceptor.ConsumerTraceBinding;
import org.acme.kafka.util.KafkaHeaderUtil;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletionStage;

@Slf4j
@ApplicationScoped
public class ReplyService {
    @Inject
    @Channel("response")
    Emitter<String> responseEmitter;

    @Incoming("request-in")
    @Traced
    @ConsumerTraceBinding
    public CompletionStage<Void> getRequest(Message<String> message) {
        String out = message.getPayload() + " " + LocalDateTime.now(); // The operation
        if (new Random().nextBoolean()) {
            sendOptionalResponse(out, message);
            return message.ack();
        }
        else {
            return message.nack(new Throwable("Test"));
        }
    }

    private void sendOptionalResponse(String out, Message<String> message){
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
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
