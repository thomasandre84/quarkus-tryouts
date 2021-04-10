package org.acme.kafka.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.acme.kafka.service.KafkaProd;
import org.acme.kafka.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Path("/")
public class KafkaController {

    @Inject
    KafkaProd kafkaProd;

    @Inject
    RequestService requestService;

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
        return requestService.process(test)
                .onItem().transform( s -> Response.ok(s).build());
    }

    @GET
    @Path("/response/{test}/2")
    @Produces(MediaType.TEXT_PLAIN)
    public Response response2(@PathParam("test") String test) {
        log.info("Received Request: {}", test);
        try {
            String resp = requestService.process2(test);
            return Response.ok(resp).build();
        } catch (TimeoutException ex) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }

    }
}