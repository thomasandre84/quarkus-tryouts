package org.acme.commandmode;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.common.template.test;

public class KafkaConsum {

    static Logger log = LoggerFactory.getLogger(KafkaConsum.class);

    @Incoming("test-in")
    public void getMessage(String test) {
        log.info("Incoming: {}", test);
    }

}
