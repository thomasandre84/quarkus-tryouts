package org.example.examples.hello;

import io.quarkus.security.identity.SecurityIdentity;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

//@Produces(MediaType.APPLICATION_JSON)
@Path(AuthResource.BASE_URL)
public class AuthResource {
    static final String BASE_URL = "/";

    @Inject
    SecurityIdentity identity;

    @GET
    public String hello() {
        return String.format("Hello, %s", identity.getPrincipal().getName());
    }

}
