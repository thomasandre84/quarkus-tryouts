package org.acme.commandmode;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class ProdConsService {
    static Logger log = LoggerFactory.getLogger(ProdConsService.class);

    BroadcastProcessor<String> processor = BroadcastProcessor.create();
    Multi<String> multi = processor.onItem().transform(String::toString);

    @Inject
    @Channel("request")
    Emitter<String> emitter;

    @Inject
    ManagedExecutor executor;

    //CustomExecutor executor = new CustomExecutor();

    //
    //UnicastProcessor<String> processor = UnicastProcessor.create();


    public Uni<String> sendMessage(String test) {
        log.info("Outgoing: {}", test);
        //Metadata metadata = new Metadata();
        emitter.send(Message.of(test,
                () -> {
                    // Called when the message is acknowledged.
                    return CompletableFuture.completedFuture(null);
                }));
        return Uni.createFrom().item(test);
    }

    @Incoming("request-in")
    @Outgoing("response")
    @Traced
    public Uni<String> doResponse(String message) {
        String out = message + LocalDateTime.now().toString();
        log.info("Modified Outgoing " +
                "Response: {}", out);
        return Uni.createFrom().item(out);
    }

    @Incoming("response-in")
    @Traced
    public void respond(String message) {
        log.info("Incoming Response: {}", message);
        processor.onNext(message);
    }

    private Object streamRespond(Object item) {
        return null;
    }


    public Uni<String> process(String message) {
        Uni<String> resp = Uni.createFrom().multi(multi);
        resp.onItem().invoke(t -> log.info("received: {}", t));
        //.subscribe().with(item -> System.out.println(item));

        Uni<String> input = sendMessage(message);
        log.info("After Send Ack: {}", input);

        Uni<String> trd = Uni.createFrom().multi(multi);
        trd.runSubscriptionOn(executor).onItem().invoke(m -> log.info("Getting message {}", m))
                .subscribe().with(item -> log.info("Sep Thread: {}", item));

        return resp;
    }

}
