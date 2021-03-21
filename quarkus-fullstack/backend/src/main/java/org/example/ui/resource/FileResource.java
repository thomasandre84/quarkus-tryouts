package org.example.ui.resource;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.example.ui.dto.FileCategoryInput;
import org.example.ui.model.FileCategory;
import org.example.ui.model.FileVersion;
import org.example.ui.dto.FileVersionActive;
import org.example.ui.service.FileService;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.io.IOException;

import static org.example.ui.resource.FileResource.BASE_URL;

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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> saveVersion(@MultipartForm MultipartFormDataInput input) throws IOException, IllegalAccessException, NoSuchFieldException {
        return fileService.persistVersion(input)
                .onItem().transform(v -> Response.ok(v).build());
    }

    @GET
    @Path("/versions/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Uni<byte[]> downloadVersion(@QueryParam("name") String name,
                                       @QueryParam("category") String category,
                                       @QueryParam("version") Integer version) {
        return fileService.downloadContent(name, category, version);
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

    @PUT
    @Path("/versions/active")
    public Uni<Response> activateVersion(FileVersionActive fileVersionActive) {
        return fileService.setVersionActive(fileVersionActive)
                .onItem().transform(v -> Response.ok().build());
    }
}
