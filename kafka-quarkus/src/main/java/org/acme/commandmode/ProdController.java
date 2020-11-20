package org.acme.commandmode;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("")
public class ProdController {

    static Logger log = LoggerFactory.getLogger(ProdController.class);

    @Inject
    KafkaProd kafkaProd;

    @GET
    @Path("/{test}")
    public void sendMessage(@PathParam("test") String test) {
        log.info(test);
        kafkaProd.setMessage(test);
    }
}