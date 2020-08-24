package org.example.examples.hello;


import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class StreamingService {

    private final Vertx vertx;

    @Inject
    public StreamingService(Vertx vertx) {
        this.vertx = vertx;
    }

    public Multi<String> getDatesForName(String name) {
        return vertx.periodicStream(2000).toMulti()
                .map(l -> String.format("Hello %s! (%s)%n", name, LocalDateTime.now()));
    }

}
