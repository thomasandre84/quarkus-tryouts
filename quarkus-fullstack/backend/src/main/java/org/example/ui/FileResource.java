package org.example.ui;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import static org.example.ui.FileResource.BASE_URL;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(BASE_URL)
public class FileResource {
    static final String BASE_URL = "api/v1/files";

    @Inject
    FileService fileService;

    @GET
    @Path("/versions")
    public Multi<FileVersion> getVersions() {
        return fileService.getVersions();
    }

    @POST
    @Path("/versions")
    public Uni<Response> saveVersion(FileVersionInput versionInput) {
        return fileService.persistVersion(versionInput)
                .onItem().transform(v -> Response.ok(v).build());
    }

    @GET
    @Path("/categories")
    public Multi<FileCategory> getCategories() {
        return fileService.getCategories();
    }

    @POST
    @Path("/categories")
    public Uni<Response> saveCategory(FileCategoryInput category) {
        return fileService.persistCategory(category)
                .onItem().transform(f -> Response.ok(f).build());
    }

    @PATCH
    @Path("/versions")
    public Uni<Response> activateVersion(FileVersionActive fileVersionActive) {
        return fileService.setVersionActive(fileVersionActive)
                .onItem().transform(v -> Response.ok().build());
    }
}
