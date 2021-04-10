package org.acme.kafka.util;

import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;

import java.util.List;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class KafkaHeaderUtil {
    static final String PARTITION = "partition";
    static final String ID = "id";

    private KafkaHeaderUtil() {
    }

    public static Optional<Integer> getTargetPartition(IncomingKafkaRecordMetadata metadata) {
        AtomicReference<Optional<Integer>> target = new AtomicReference<>(Optional.empty());
        Headers headers = metadata.getHeaders();
        headers.forEach(header -> {
            if (PARTITION.equals(header.key())) {
                BigInteger bi = new BigInteger(header.value());
                Integer targetPartition = bi.intValue();
                log.info("Target Partition: {}", targetPartition);
                target.set(Optional.of(targetPartition));
            }
        });

        return target.get();
    }

    public static Optional<String> getHeaderId(IncomingKafkaRecordMetadata metadata) {
        AtomicReference<Optional<String>> iD = new AtomicReference<>(Optional.empty());
        Headers headers = metadata.getHeaders();
        headers.forEach(header -> {
            if (ID.equals(header.key())) {
                String id = new String(header.value());
                log.info("Got UUID of: {}", id);
                iD.set(Optional.of(id));
            }
        });
        return iD.get();
    }

    public static Boolean equalId(String initId, Optional<String> receivedId) {
        String recId = receivedId.get();
        log.info("Init Id {}  with received Id: {}", initId, recId);
        return initId.equals(recId);
    }

    public static BigInteger getReplyTargetPartition(List<TopicPartition> partitionList) {
        int partitionSize = partitionList.size();
        int partitionIndex = new Random().nextInt(partitionSize);
        TopicPartition target = partitionList.get(partitionIndex); // for tests choose a fix partition
        BigInteger targetPartition = BigInteger.valueOf(target.partition());
        log.info("Identified TargetPartition: {}", targetPartition);
        return targetPartition;
    }

    public static String getGeneratedId() {
        String id = UUID.randomUUID().toString();
        log.info("Generated UUID: {}", id);
        return id;
    }
}
