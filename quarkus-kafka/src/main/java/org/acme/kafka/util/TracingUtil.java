package org.acme.kafka.util;

import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Tracer;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.Collections;

public class TracingUtil {

    public static void emitMessageWithTrace(Emitter<String> notificationEmitter, Tracer tracer, String json) {

        // getting the context
        JaegerSpanContext spanCtx = ((JaegerSpan) tracer.activeSpan()).context();

        String uberTraceId = constructUberTraceId(spanCtx);

        // constructing kafka message from json and adding uber-trace-id in the header
        Message message = TracingUtil.constructMessageWithTrace(uberTraceId, json);

        notificationEmitter.send(message);
    }

    private static String constructUberTraceId(JaegerSpanContext spanCtx) {
        return spanCtx.getTraceId() + ":" +
                Long.toHexString(spanCtx.getSpanId()) + ":" +
                Long.toHexString(spanCtx.getParentId()) + ":" +
                Integer.toHexString(spanCtx.getFlags());
    }

    private static Message constructMessageWithTrace(String uberTraceId, String json) {
        RecordHeader header = new RecordHeader("uber-trace-id", uberTraceId.getBytes());

        Message message = Message.of(json);
        OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder()
                .withHeaders(Collections.singletonList(header))
                .build();

        message = message.addMetadata(metadata);
        return message;
    }
}
