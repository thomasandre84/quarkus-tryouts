package org.example.ui.resource;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(RuntimeConfigResource.BASE_URL)
public class RuntimeConfigResource {
    static final String BASE_URL = "api/v1/runtime";

    @GET
    public Uni<Response> getRuntimeConfig() {
        return Uni.createFrom().item(Response.ok().build());
    }
}
