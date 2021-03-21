package org.example.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.message.BodyPart;
import org.example.ui.dto.FileCategoryInput;
import org.example.ui.dto.FileVersionInput;
import org.example.ui.model.FileActive;
import org.example.ui.model.FileCategory;
import org.example.ui.model.FileVersion;
import org.example.ui.dto.FileVersionActive;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ApplicationScoped
public class FileService {

    static ObjectMapper mapper = new ObjectMapper();

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

    private static Stream<FileVersion> getFileVersions(String name, FileCategory category) {
        return FileVersion.find("name = :name and category = :category order by version",
                Parameters.with("name", name).and("category", category))
                .stream();
    }

    private static Stream<FileActive> getActiveFiles(FileVersion version, FileCategory category) {
        return FileActive.find("version.name = :version and category = :category",
                Parameters.with("version", version.getName()).and("category", category))
                .stream();
    }

    private static Optional<FileCategory> getCategory(String category) {
        return FileCategory.findByIdOptional(category);
    }

    private static FileVersion getFileVersion(FileVersion.FileVersionId id) {
        return FileVersion.findById(id);
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

    public Uni<byte[]> downloadContent(String name, String category, Integer version) {
        Optional<FileCategory> fileCategory = getCategory(category);
        if (fileCategory.isPresent()) {
            FileVersion.FileVersionId id = FileVersion.FileVersionId.builder()
                    .name(name).category(fileCategory.get()).version(version).build();

            return Uni.createFrom().item(id)
                    .onItem().transform(FileService::getFileVersion)
                    .onItem().transform(FileVersion::getContent)
                    .onItem().transform(content -> content.getBytes(StandardCharsets.UTF_8));
        }
        return Uni.createFrom().nullItem();
    }

    @Transactional
    public synchronized Uni<FileVersion> persistVersion(MultipartFormDataInput input) throws IOException, IllegalAccessException, NoSuchFieldException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        String obj = getStringData(getRelevantInputPart(uploadForm.get("obj")));
        FileVersionInput versionInput = mapper.readValue(obj, FileVersionInput.class);

        Optional<FileCategory> category = getCategory(versionInput.getCategory());

        if (category.isPresent()) {
            // Do the work only, if the relevant data fields are correct
            List<InputPart> fileInputParts = uploadForm.get("file");
            InputPart fileInputPart = getRelevantInputPart(fileInputParts);

            byte[] data = getFileData(fileInputPart);
            assert data != null;
            String content = new String(data);

            Integer version = getNextVersion(versionInput.getName(), category.get());
            return Uni.createFrom()
                    .item(versionInput)
                    .onItem().transform(ver -> FileVersion.builder()
                            .version(version)
                            .name(versionInput.getName())
                            .category(category.get())
                            .content(content)
                            .build()
                    )
                    .onItem().invoke(FileService::accept);

        }
        return Uni.createFrom().failure(new NotFoundException("Category unknown"));
    }

    //TODO
    @Transactional
    public synchronized Uni<Void> setVersionActive(FileVersionActive active) {
        Optional<FileCategory> category = getCategory(active.getCategory());
        if (category.isPresent()) {
            FileVersion.FileVersionId versionId = FileVersion.FileVersionId.builder()
                    .name(active.getName()).category(category.get()).version(active.getVersion()).build();
            Optional<FileVersion> fileVersion = FileVersion.findByIdOptional(versionId);
            if (fileVersion.isPresent()) {
                Stream<FileActive> activeStream = getActiveFiles(fileVersion.get(), category.get());
                List<FileActive> activeList = activeStream.collect(Collectors.toList());
                if (!activeList.isEmpty()){
                    // delete all former active Versions
                    log.info("Going to delete former Active Versions with category '{}' and version '{}'",
                            category.get().getCategory(), fileVersion.get().getName());
                    activeList.forEach(PanacheEntityBase::delete);
                }
                FileActive fileActive = FileActive.builder()
                        .active(active.getVersion()).version(fileVersion.get()).category(category.get()).build();
                fileActive.persist();

                fileVersion.get().setActivated(Instant.now());
                fileVersion.get().persist();
            }
        }
        return Uni.createFrom().voidItem();
    }

    private static Integer getNextVersion(String name, FileCategory category) {
        Stream<FileVersion> fileVersions = getFileVersions(name, category);
        List<Integer> versions = fileVersions.map(FileVersion::getVersion).collect(Collectors.toList());
        if (versions.isEmpty()){
            return 1;
        } else {
            return versions.stream().max(Integer::compare).get() + 1;
        }
    }

    private InputPart getRelevantInputPart(List<InputPart> inputParts) {
        return inputParts.get(0);
    }


    private byte[] getFileData(InputPart inputPart) {
        try {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            return inputStream.readAllBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStringData(InputPart inputPart) {
        try {
            return inputPart.getBody(String.class, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
