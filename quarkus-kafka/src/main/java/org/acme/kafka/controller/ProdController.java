package org.acme.kafka.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.smallrye.mutiny.Uni;
import org.acme.kafka.service.KafkaProd;
import org.acme.kafka.service.ProdConsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class ProdController {

    static Logger log = LoggerFactory.getLogger(ProdController.class);

    @Inject
    KafkaProd kafkaProd;

    @Inject
    ProdConsService prodConsService;

    @GET
    @Path("/{test}")
    public void sendMessage(@PathParam("test") String test) {
        log.info(test);
        kafkaProd.setMessage(test);
    }

    @GET
    @Path("/response/{test}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> response(@PathParam("test") String test) {
        log.info("Received Request: {}", test);
        return prodConsService.process(test)
                .onItem().transform( s -> Response.ok(s).build());
    }
}