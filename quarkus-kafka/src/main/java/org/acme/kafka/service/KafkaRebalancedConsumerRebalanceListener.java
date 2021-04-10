package org.acme.kafka.service;

import io.smallrye.reactive.messaging.kafka.KafkaConsumerRebalanceListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;


@ApplicationScoped
@Named("response-incoming")
@Slf4j
public class KafkaRebalancedConsumerRebalanceListener implements KafkaConsumerRebalanceListener {

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
        topicPartitions.clear();
        for (TopicPartition partition : partitions) {
            log.info("Assigned Partition: {}",  partition);
            topicPartitions.add(partition);
        }
    }

    public List<TopicPartition> getTopicPartitions() {
        return topicPartitions;
    }

}