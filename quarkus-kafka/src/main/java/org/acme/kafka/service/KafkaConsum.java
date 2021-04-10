package org.acme.kafka.service;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaConsum {

    static Logger log = LoggerFactory.getLogger(KafkaConsum.class);

    @Incoming("test-in")
    //@Traced
    public void getMessage(String test) {
        log.info("Incoming: {}", test);
    }

}
