package com.github.thomasandre84.consents.repository;

import com.github.thomasandre84.consents.domain.Consent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public interface ConsentRepository extends PanacheRepositoryBase<Consent, UUID> {
}
