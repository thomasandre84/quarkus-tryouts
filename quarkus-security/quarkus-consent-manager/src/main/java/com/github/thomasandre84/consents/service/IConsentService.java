package com.github.thomasandre84.consents.service;

import com.github.thomasandre84.consents.domain.dto.ConsentDto;
import io.smallrye.mutiny.Multi;

public interface IConsentService {
    Multi<ConsentDto> getConsentsByServiceAndClient(String service, String client);
}
