package org.acme.commandmode;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;

@ApplicationScoped
public class KafkaProd {
    static Logger log = LoggerFactory.getLogger(KafkaProd.class);
    static UnicastProcessor<String> processor = UnicastProcessor.create();

    Multi<String> multi = processor.onItem().transform(String::toUpperCase).onFailure().recoverWithItem("d'oh");

    @Outgoing("test-out")
    public Multi<String> pubMessage() {
        return multi;
    }

    public void setMessage(String test) {
        log.info("Outgoing: {}", test);
        processor.onNext(test);
    }

}