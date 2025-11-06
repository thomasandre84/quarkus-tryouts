package com.github.thomasandre84.sftp;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;

@Path("/sftp")
public class SftpResource {

    @Inject
    SftpLockRepository repository;
    @Inject SftpPoolService poolService;

    @GET
    public List<SftpLock> findAll() {
        return repository.listAll();
    }

    @POST
    @Transactional
    public void create() {
        SftpLock lock = new SftpLock();
        repository.persist(lock);
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
