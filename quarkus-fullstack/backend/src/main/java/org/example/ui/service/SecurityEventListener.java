package org.example.ui.service;
/*
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.oidc.IdTokenCredential;
import io.quarkus.oidc.SecurityEvent;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class SecurityEventListener {

    public void event(@Observes SecurityEvent event) {
        String tenantId = event.getSecurityIdentity().getAttribute("tenant-id");
        RoutingContext vertxContext = event.getSecurityIdentity().getCredential(IdTokenCredential.class).getRoutingContext();
        vertxContext.put("listener-message", String.format("event:%s,tenantId:%s", event.getEventType().name(), tenantId));
    }
}
*/