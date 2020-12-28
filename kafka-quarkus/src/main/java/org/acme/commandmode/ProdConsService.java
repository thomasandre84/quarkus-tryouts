package org.acme.commandmode;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    //
    //UnicastProcessor<String> processor = UnicastProcessor.create();
    Multi<String> multi = Multi.createFrom().empty();

    public void sendMessage(String test) {
        log.info("Outgoing: {}", test);
        emitter.send(test);
    }

    @Incoming("request-in")
    @Outgoing("response")
    public Uni<String> doResponse(String message) {
        String out = message + LocalDateTime.now().toString();
        log.info("Modified Outgoing " +
                "Response: {}", out);
        return Uni.createFrom().item(out);
    }

    @Incoming("response-in")
    public Uni<String> respond(String message) {
        log.info("Incoming Response: {}", message);
        return Uni.createFrom().item(message);
    }

    @Outgoing("request")
    public Multi<String> sendOut() {
        return multi;
    }

    public void process(String message) {
        Multi<String> multiProcess = Multi.createFrom().item(message)
                //.onItem()
                .call(this::respond);
                //.map()

    }

}
