package org.acme.kafka.service;

import io.smallrye.reactive.messaging.kafka.KafkaConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;


@ApplicationScoped
@Named("response-incoming")
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
        topicPartitions.clear();
        for (org.apache.kafka.common.TopicPartition partition : partitions) {
            LOGGER.info("Assigned Partition: {}",  partition);
            topicPartitions.add(partition);
        }
    }

    public List<TopicPartition> getTopicPartitions() {
        return topicPartitions;
    }

}