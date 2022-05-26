package com.github.thomasandre84.mtls;

import io.quarkus.security.identity.SecurityIdentity;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


//@Produces(MediaType.APPLICATION_JSON)
@Path(AuthResource.BASE_URL)
public class AuthResource {
    static final String BASE_URL = "/hello";

    @Inject
    SecurityIdentity identity;

    @GET
    public String hello() {
        return String.format("Hello, %s", identity.getPrincipal().getName());
    }

}
