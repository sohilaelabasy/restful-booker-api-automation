package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testng.Assert.assertEquals;

public class HealthCheckTest extends BaseTest {
    @Test
    public void testHealthCheckReturns201() {
        Response response =given(spec)
                .when()
                .get("/ping");
        assertEquals(response.statusCode() , 201);
    }

    //Test Case 2: POST to /ping instead of GET
    @Test
    public void testHealthCheckWithPostReturns405() {
        Response response = given(spec)
                .when()
                .post("/ping");
        assertThat(response.statusCode())
                .as("POST should not be allowed on /ping")
                .isIn(400, 404, 405);
     }
    // Test Case 3 — Random body sent to ping

    @Test
    public void testPingWithRandomBody() {
        Response response = given(spec)
                .body("{ \"random\": \"data\" }")
                .when().get("/ping");

        assertThat(response.statusCode()).isEqualTo(201);
    }
}
