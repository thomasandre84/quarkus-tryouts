package org.example.ui;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


import static org.example.ui.FileResource.BASE_URL;

@Path(BASE_URL)
public class FileResource {
    static final String BASE_URL = "api/v1/files";

    @Inject
    FileService fileService;

    @GET
    @Path("/versions")
    public Multi<Object> getVersions() {
        return Multi.createFrom().empty();
    }

    @POST
    @Path("/versions")
    public Uni<Response> saveVersion() {
        return null;
    }



    @GET
    @Path("/categories")
    public Multi<FileCategory> getCategories() {
        return fileService.getCategories();
    }

    @POST
    @Path("/categories")
    public Uni<Response> saveCategory(FileCategory fileCategory) {
        return fileService.persistCategory(fileCategory)
                .onItem().transform(f -> Response.ok(f).build());
    }
}
