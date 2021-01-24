package org.acme.resteasyjackson.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.acme.resteasyjackson.service.DBWriter;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Path(InitResource.BASE_URL)
public class InitResource {
    static final String BASE_URL = "/start";
    private final DBWriter DBWriter;
    private final ManagedExecutor managedExecutor;


    @GET
    @Path("/single")
    public void init() {
        DBWriter.runSingleBatch();
    }

    @GET
    @Path("/singlereactive")
    public void initreactive() {
        DBWriter.runSingleReactive();
    }

    @GET
    @Path("/asyncreactive")
    public void runAsync() {
        managedExecutor.runAsync(() -> DBWriter.runMultiReactive());
    }


}
