package com.github.thomasandre84.consents.service;

import com.github.thomasandre84.consents.domain.dto.ConsentDto;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConsentService implements IConsentService{

    private final TokenRequestService tokenRequestService;

    @Inject
    public ConsentService(@RestClient TokenRequestService tokenRequestService) {
        this.tokenRequestService = tokenRequestService;
    }

    @Override
    public Multi<ConsentDto> getConsentsByServiceAndClient(String service, String client) {
        return null;
    }

    public void getToken() {
        tokenRequestService.getToken(null);
    }

}
