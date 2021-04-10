package org.acme.quartz;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TaskResourceTest {

    @Test
    public void tasks() throws InterruptedException {
        Thread.sleep(1000); // wait at least a second to have the first task created
        given()
                .when().get(SchedulesRessource.BASE_URL)
                .then()
                .statusCode(200)
                .body("size()", is(greaterThanOrEqualTo(1)));
    }
}