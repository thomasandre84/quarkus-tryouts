package org.acme.kafka.service;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.acme.kafka.util.KafkaHeaderUtil;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Slf4j
@ApplicationScoped
public class ReplyService {
    @Inject
    @Channel("response")
    Emitter<String> responseEmitter;

    @Inject
    Tracer tracer;

    @Incoming("request-in")
    @Traced
    public CompletionStage<Void> getRequest(Message<String> message) {
        String out = message.getPayload() + " " + LocalDateTime.now(); // The operation
        sendOptionalResponse(out, message);
        return message.ack();
    }

    private void sendOptionalResponse(String out, Message<String> message){
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        if (metadata.isPresent()) {
            Optional<Integer> targetPartition = KafkaHeaderUtil.getTargetPartition(metadata.get());
            Optional<String> id = KafkaHeaderUtil.getHeaderId(metadata.get());
            if (targetPartition.isPresent() && id.isPresent()) {
                // Also extract TraceId and add it again to metadata
                //tracer.scopeManager().active().span().setBaggageItem("traceId", id.get());
                //final SpanContext spanContext = tracer.scopeManager().active().span().context();
                //((JaegerSpanContext) spanContext).
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
