package org.acme.filerest;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Startup
public class FileContentService {

    @ConfigProperty(name = "email.domain.file.path")
    String inputFilePath;

    @Inject
    Vertx vertx;


    private Set<String> domains;

    public Set<String> getDomains() {
        return domains;
    }

    @PostConstruct
    void init() {
        Path path = Path.of(inputFilePath);
        vertx.fileSystem().readFile(inputFilePath)
                .onItem().transform(buffer -> buffer.toString("UTF-8"))
                .subscribe()
                .with(
                        content -> domains = getSetOfStrings(content),
                        err -> System.out.println("Cannot read the file: " + err.getMessage())
                );
    }

    static Set<String> getSetOfStrings(String content){
        return Arrays.stream(content.split("\n"))
                .map(String::strip)
                .collect(Collectors.toSet());
    }

    public Uni<Set<String>> getAllDomains(){
        return Uni.createFrom().item(domains);
    }

    public Uni<Boolean> isFreeDomain(String domain) {
        return Uni.createFrom().item(domain)
                .onItem().transform(d -> domains.contains(d));
    }
}
