package org.example.examples.hello;

/*
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello")
public class HelloWorldEndpoint {

    @Inject
    @GrpcService("hello")
    GreeterGrpc.GreeterBlockingStub blockingHelloService;
    @Inject
    @GrpcService("hello")
    MutinyGreeterGrpc.MutinyGreeterStub mutinyHelloService;

    @GET
    @Path("/blocking/{name}")
    public String helloBlocking(@PathParam("name") String name) {
        HelloReply reply = blockingHelloService.sayHello(HelloRequest.newBuilder().setName(name).build());
        return generateResponse(reply);

    }

    @GET
    @Path("/mutiny/{name}")
    public Uni<String> helloMutiny(@PathParam("name") String name) {
        return mutinyHelloService.sayHello(HelloRequest.newBuilder().setName(name).build())
                .onItem().apply((reply) -> generateResponse(reply));
    }

    public String generateResponse(HelloReply reply) {
        return String.format("%s! HelloWorldService has been called %d number of times.", reply.getMessage(), reply.getCount());
    }
}*/
