package org.example.examples.hello;

import io.smallrye.mutiny.Multi;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path(StreamingResource.BASE_URL)
public class StreamingResource {
    static final String BASE_URL = "/stream";

    private final StreamingService streamingService;

    @Inject
    public StreamingResource(StreamingService streamingService){
        this.streamingService = streamingService;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/{name}")
    public Multi<String> greeting(@PathParam("name") String name) {
        return streamingService.getDatesForName(name);
    }

}
