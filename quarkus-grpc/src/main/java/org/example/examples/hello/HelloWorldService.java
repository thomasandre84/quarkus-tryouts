package org.example.examples.hello;

import io.smallrye.mutiny.Uni;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;
/*
@Singleton
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
*/