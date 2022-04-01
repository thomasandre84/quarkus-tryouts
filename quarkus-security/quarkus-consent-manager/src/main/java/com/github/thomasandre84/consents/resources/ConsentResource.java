package com.github.thomasandre84.consents.resources;

import com.github.thomasandre84.consents.domain.dto.ConsentDto;
import com.github.thomasandre84.consents.service.IConsentService;
import io.smallrye.mutiny.Multi;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(ConsentResource.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ConsentResource {
    static final String BASE_URL = "/consents";

    private final IConsentService consentService;


    @GET
    public Multi<ConsentDto> getConsents(
            @QueryParam("service") String service,
            @QueryParam("client") String client
    ) {
        return consentService.getConsentsByServiceAndClient(service, client);
    }
}