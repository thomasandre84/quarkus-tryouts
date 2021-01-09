package org.acme.resteasyjackson.resource;

import org.acme.resteasyjackson.model.Base64Input;
import org.acme.resteasyjackson.model.FileContent;
import org.acme.resteasyjackson.service.FileContentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path(FileContentResource.BASE_URL)
public class FileContentResource {
    static final String BASE_URL = "/files";
    private final FileContentService fileContentService;

    @Inject
    public FileContentResource(FileContentService fileContentService) {
        this.fileContentService = fileContentService;
    }

    @GET
    public Response getAllFiles() {
        return Response.ok(fileContentService.getAllFiles()).build();
    }

    @POST
    public Response saveFile(Base64Input input) {
        FileContent fileContent = fileContentService.saveFile(input);
        return Response.created(URI.create(BASE_URL + "/" + fileContent.getId())).build();
    }

    @GET
    @Path("/{id}")
    public Response getFile(@PathParam("id") Long id) {
        return Response.ok(FileContent.findByIdOptional(id)).build();
    }

}
