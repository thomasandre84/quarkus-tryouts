package org.acme.resteasyjackson.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import lombok.extern.slf4j.Slf4j;
import org.acme.resteasyjackson.model.ExampleEntity;
import org.acme.resteasyjackson.repository.ExampleEntityRepository;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@ApplicationScoped
public class DBWriter {

    static final int AMOUNT = 100000;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    ExampleEntityRepository exampleEntityRepository;

    Random random = new Random();

    /**
     * Can be done in One Transaction
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void runSingleBatch() {
        //runBlockingSingleThreaded(amount);
        List<ExampleEntity> entities = genExamples(AMOUNT);
        log.info("With amount: {} - Start Time: {}", AMOUNT, LocalDateTime.now());
        entities.forEach(e -> e.persist());
        log.info("End Time: {}", LocalDateTime.now());
    }


    /**
     * Can be done in One Transaction
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void runSingleReactive() {
        log.info("With amount: {} - Start Time: {}", AMOUNT, LocalDateTime.now());
        Multi.createFrom().iterable(this.genExamples(AMOUNT))
                //.onItem().invoke(e -> log.info("Item: {}", e))
                //.onItem().invoke(e -> e.persist())
                .subscribe().with(
                    item -> item.persist(),
                    failure -> log.warn("A Failure happened", failure),
                    () -> log.info("Multi finished at: {}", LocalDateTime.now())
        );
    }

    /**
     * We have to do in many Transactional - slower
     */
    public void runMultiReactive() {
        log.info("With amount: {} - Start Time: {}", AMOUNT, LocalDateTime.now());
        Multi.createFrom().iterable(this.genExamples(AMOUNT))
                .emitOn(Infrastructure.getDefaultExecutor())
                //.onItem().invoke(e -> log.info("{}", e))
                .subscribe().with(
                item -> exampleEntityRepository.persistTransactional(item),
                failure -> log.warn("A Failure happened", failure),
                () -> log.info("Multi finished at: {}", LocalDateTime.now())
        );
    }

    private List<ExampleEntity> genExamples(int amount) {
        List<ExampleEntity> exampleEntities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            exampleEntities.add(genExample());
        }
        return exampleEntities;
    }

    private ExampleEntity genExample() {
        return ExampleEntity.builder()
                .random(random.nextLong())
                .build();
    }
}
