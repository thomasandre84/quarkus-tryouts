package com.github.thomasandre84.sftp;

import com.github.thomasandre84.sftp.channel.SftpChannelService;
import com.github.thomasandre84.sftp.client.SftpPoolService;
import com.github.thomasandre84.sftp.db.SftpLock;
import com.github.thomasandre84.sftp.db.SftpLockHandler;
import com.github.thomasandre84.sftp.session.SshSessionService;
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

    @Inject
    SftpChannelService channelService;


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
    public List<String> listSftpHomeDir(@PathParam("host") String host) {
        return sshSessionService.listSftpHomeDir(host);
    }

    @POST
    @Path("/sessions/async/{host}/{amount}")
    public void listSftpHomeDirAsync(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        sshSessionService.listSftpHomeDirAsync(host, amount);
    }

    @POST
    @Path("/sessions/channel/{host}")
    public List<String> listSftpHomeDirChannel(@PathParam("host") String host) {
        return channelService.listDirAsync(host);
    }

    @POST
    @Path("/sessions/channel/async/{host}/{amount}")
    public void listSftpHomeDirAsyncChannel(@PathParam("host") String host, @PathParam("amount") int amount) throws Exception {
        channelService.listDirAsync(host, amount);
    }

}
