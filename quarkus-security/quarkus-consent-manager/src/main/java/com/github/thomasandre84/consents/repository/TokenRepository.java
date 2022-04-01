package com.github.thomasandre84.consents.repository;

import com.github.thomasandre84.consents.domain.Token;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface TokenRepository extends PanacheRepositoryBase<Token, String> {
}
