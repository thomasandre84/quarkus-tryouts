package org.example.examples.hello;

import examples.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
public class HelloWorldService extends MutinyGreeterGrpc.GreeterImplBase {

    AtomicInteger counter = new AtomicInteger();

    @Override
    public Uni<HelloReply> sayHello(HelloRequest request) {
        int count = counter.incrementAndGet();
        String name = request.getName();
        return Uni.createFrom().item("Hello " + name)
                .map(res -> HelloReply.newBuilder().setMessage(res).setCount(count).build());
    }
}