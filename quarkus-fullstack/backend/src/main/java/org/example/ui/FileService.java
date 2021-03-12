package org.example.ui;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Slf4j
@ApplicationScoped
public class FileService {

    @Inject
    EntityManager em;

    private static void accept(FileCategory f) {
        f.persist();
    }

    public Multi<FileCategory> getCategories() {
        return Multi.createFrom()
                .items(FileCategory.findAll().stream());
    }

    @Transactional
    public Uni<FileCategory> persistCategory(FileCategory fileCategory) {
        return Uni.createFrom()
                .item(fileCategory)
                .onItem().invoke(FileService::accept);
    }

}
