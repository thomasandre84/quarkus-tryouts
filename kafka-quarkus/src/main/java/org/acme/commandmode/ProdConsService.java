package org.acme.commandmode;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);

    @Incoming("test-out")
    public Uni<String> getOutput(Message<String> message) {
        log.info("Got Message: {}", message);
        return Uni.createFrom().item(message.getPayload());
    }
}
