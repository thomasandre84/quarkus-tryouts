package org.example.examples.hello;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static io.restassured.RestAssured.given;

import static org.example.examples.hello.StreamingResource.BASE_URL;

@QuarkusTest
@Tag("integration")
class StreamingResourceTest {

    private static final String name = "test";

    @Test
    void greeting() {
        given()
                .when().get(BASE_URL+"/"+name)
                .then()
                .statusCode(200);
    }
}