package org.example.ui;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class FileService {

    @Inject
    EntityManager em;

    private static void accept(FileCategory cat) {
        log.info("Persist Category: {}", cat);
        cat.persist();
    }

    private static void accept(FileVersion ver) {
        log.info("Persist Version: {}", ver);
        ver.persist();
    }

    public Multi<FileCategory> getCategories() {
        return Multi.createFrom()
                .items(FileCategory.findAll().stream());
    }

    @Transactional
    public Uni<FileCategory> persistCategory(FileCategoryInput category) {
        return Uni.createFrom()
                .item(category)
                .onItem().transform(cat -> FileCategory.builder().category(cat.getCategory()).build())
                .onItem().invoke(FileService::accept);
    }

    public Multi<FileVersion> getVersions() {
        return Multi.createFrom()
                .items(FileVersion.findAll().stream());
    }

    @Transactional
    public synchronized Uni<FileVersion> persistVersion(FileVersionInput versionInput) {
        Optional<FileCategory> category = FileCategory.findByIdOptional(versionInput.getCategory());
        if (category.isPresent()) {
            Integer version = getNextVersion(versionInput.getName(), category.get());
            return Uni.createFrom()
                    .item(versionInput)
                    .onItem().transform(ver -> FileVersion.builder()
                            .version(version)
                            .name(versionInput.getName())
                            .category(category.get())
                            .content(versionInput.getContent())
                            .build()
                    )
                    .onItem().invoke(FileService::accept);

        }
        return Uni.createFrom().failure(new Throwable("Category unknown"));
    }

    private Integer getNextVersion(String name, FileCategory category) {
        Stream<FileVersion> fileVersions = FileVersion.find("name = :name and category = :category order by version",
                Parameters.with("name", name).and("category", category))
                .stream();
        List<Integer> versions = fileVersions.map(FileVersion::getVersion).collect(Collectors.toList());
        if (versions.isEmpty()){
            return 1;
        } else {
            return versions.stream().max(Integer::compare).get() + 1;
        }
    }
}
