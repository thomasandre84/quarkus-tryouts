package org.example.examples.hello;


import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class HelloWorldService {

    AtomicInteger counter = new AtomicInteger();

}
