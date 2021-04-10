package org.acme.quartz;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static org.acme.quartz.SchedulesRessource.BASE_URL;

@Path(BASE_URL)
public class SchedulesRessource {
    static final String BASE_URL = "schedules";

    @GET
    public Response getAllSchedules() {
        return Response.ok(Task.listAll()).build();
    }
}
