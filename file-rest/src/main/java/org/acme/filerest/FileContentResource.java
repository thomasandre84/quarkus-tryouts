package org.acme.filerest;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/domains")
public class FileContentResource {
    @Inject
    FileContentService fileContentService;

    @GET
    public Uni<Response> getDomains(){
        return fileContentService.getAllDomains()
                .onItem().transform(i -> Response.ok(i).build());
    }

    @GET
    @Path("/{domain}")
    public Uni<Response> isFree(@PathParam("domain") String domain) {
        return fileContentService.isFreeDomain(domain)
                .onItem().transform(b -> Response.ok(b).build());
    }
}
