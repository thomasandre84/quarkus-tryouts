package org.acme.resteasyjackson.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import lombok.extern.slf4j.Slf4j;
import org.acme.resteasyjackson.model.ExampleEntity;


import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;


@Slf4j
@ApplicationScoped
public class ExampleEntityRepository implements PanacheRepositoryBase<ExampleEntity, Long> {

    /**
     * Can be done in One Transaction
     */
    @Transactional
    public void persistTransactional(ExampleEntity exampleEntity) {
        //log.info("Persisting {}", exampleEntity);
        exampleEntity.persist();
    }



}
