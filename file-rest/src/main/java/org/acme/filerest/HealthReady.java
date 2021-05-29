package org.acme.filerest;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import javax.inject.Inject;

@Readiness
@Liveness
public class HealthReady implements HealthCheck {
    @Inject
    FileContentService fileContentService;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder()
                .name("ContentLoaded")
                .state(!fileContentService.getDomains().isEmpty())
                .build();
    }
}
