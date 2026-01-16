package com.github.thomasandre84.sftp;

import com.github.thomasandre84.sftp.db.SftpLock;
import com.github.thomasandre84.sftp.db.SftpLockHandler;
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
    SftpPoolService sftpPoolService;

    @Inject
    SshSessionService sshSessionService;


    @GET
    public List<SftpLock> findAll() {
        return handler.getAllLocks();
    }

    @POST
    @Path("/{host}")
    public SftpLock create(@PathParam("host") String host) {
        return handler.addNew(host);
    }

    @POST
    @Path("/local/{host}/{amount}")
    public void createLocalPooledConnection(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        sftpPoolService.createLocalPooledConnections(host, amount);
    }

    @POST
    @Path("/local/async/{host}/{amount}")
    public void createLocalAsyncPooledConnection(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        sftpPoolService.createLocalPooledAsync(host, amount);
    }

    @POST
    @Path("/sessions/{host}")
    public void listSftpHomeDir(@PathParam("host") String host) {
        sshSessionService.listSftpHomeDir(host);
    }

    @POST
    @Path("/sessions/async/{host}/{amount}")
    public void listSftpHomeDirAsync(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        sshSessionService.listSftpHomeDirAsync(host, amount);
    }
}
