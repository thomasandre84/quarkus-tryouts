package com.github.thomasandre84.db;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.util.List;

@Path("/intervals")
public class IntervalRessource {

    @Inject
    private IntervalService intervalService;

    @GET
    public List<IntervalRelation> getIntervals() {
        return intervalService.getIntervalRelations();
    }

    @POST
    @Path("/createInterval")
    public Interval createInterval() {
        return intervalService.createInterval();
    }

    @POST
    @Path("/createInitialInterval")
    public InitialInterval createInitialInterval() {
        return intervalService.createInitialInterval();
    }

    @POST
    @Path("/createIntervalRelation/{initialId}/{intervalId}")
    public IntervalRelation createIntervalRelation(@PathParam("initialId") int initialId, @PathParam("intervalId") int intervalId) {
        return intervalService.createIntervalRelation(initialId, intervalId);
    }

    @DELETE
    @Path("/deleteInterval/{intervalId}")
    public void deleteInterval(@PathParam("intervalId") int intervalId) {
        intervalService.deleteInterval(intervalId);
    }
}
