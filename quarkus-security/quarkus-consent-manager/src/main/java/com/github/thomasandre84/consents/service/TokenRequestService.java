package com.github.thomasandre84.consents.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.Map;

@RegisterRestClient(configKey = "token-api")
@Path("/token")
public interface TokenRequestService {

    @GET
    @Path("/oauth")
    Map<String, String> getToken(@QueryParam("id") String id);


}
