package org.acme.kafka;

import io.smallrye.reactive.messaging.kafka.KafkaConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;


@ApplicationScoped
@Named("rebalanced-example.rebalancer")
public class KafkaRebalancedConsumerRebalanceListener implements KafkaConsumerRebalanceListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRebalancedConsumerRebalanceListener.class);

    private List<TopicPartition> topicPartitions = new ArrayList<>();
    /**
     * When receiving a list of partitions will search for the earliest offset within 10 minutes
     * and seek the consumer to it.
     *
     * @param consumer   underlying consumer
     * @param partitions set of assigned topic partitions
     */
    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer,
                                     Collection<org.apache.kafka.common.TopicPartition> partitions) {
        //long now = System.currentTimeMillis();
        //long shouldStartAt = now - 600_000L; //10 minute ago
        topicPartitions.clear();
        //Map<org.apache.kafka.common.TopicPartition, Long> request = new HashMap<>();
        for (org.apache.kafka.common.TopicPartition partition : partitions) {
            LOGGER.info("Assigned Partition: {}",  partition);
            topicPartitions.add(partition);
        }
        /*Map<org.apache.kafka.common.TopicPartition, OffsetAndTimestamp> offsets = consumer
                .offsetsForTimes(request);
        for (Map.Entry<org.apache.kafka.common.TopicPartition, OffsetAndTimestamp> position : offsets.entrySet()) {
            long target = position.getValue() == null ? 0L : position.getValue().offset();
            LOGGER.info("Seeking position {} for {}", target, position.getKey());
            consumer.seek(position.getKey(), target);
        }*/
    }

    public List<TopicPartition> getTopicPartitions() {
        return topicPartitions;
    }

}