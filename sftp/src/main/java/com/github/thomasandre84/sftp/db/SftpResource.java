package com.github.thomasandre84.sftp.db;

import com.github.thomasandre84.sftp.SftpPoolService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/sftp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SftpResource {

    @Inject
    SftpLockHandler handler;
    @Inject
    SftpPoolService poolService;

    @GET
    public List<SftpLock> findAll() {
        return handler.getAllLocks();
    }

    @POST
    @Path("/{target}")
    public SftpLock create(@PathParam("target") String target) {
        return handler.addNew(target);
    }

    @POST
    @Path("/local/{host}/{amount}")
    public void createLocalPooledConnection(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        poolService.createLocalPooledConnections(host, amount);
    }

    @POST
    @Path("/local/async/{host}/{amount}")
    public void createLocalAsyncPooledConnection(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        poolService.createLocalPooledAsync(host, amount);
    }
}
