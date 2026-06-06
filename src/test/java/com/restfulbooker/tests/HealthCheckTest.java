package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testng.Assert.assertEquals;

@Epic("Restful Booker API")
@Feature("System Health")
public class HealthCheckTest extends BaseTest {

    // Test Case 1 - Health check returns 201
    @Story("Verify API Availability")
    @Description("Positive Test: Verify that GET /ping returns 201 Created, confirming the API is up and running.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke", "regression"})
    public void testHealthCheckReturns201() {

        Response response = given(spec)
                .when()
                .get("/ping");

        assertEquals(response.statusCode(), 201);
    }

    // Test Case 2 - POST request to ping endpoint
    @Test(groups = {"negative"})
    @Story("Verify API Availability")
    @Description("Negative Test: Verify that POST /ping is not allowed and returns an appropriate error code (405).")
    @Severity(SeverityLevel.CRITICAL)
    public void testHealthCheckWithPostReturns405() {

        Response response = given(spec)
                .when()
                .post("/ping");

        assertThat(response.statusCode())
                .as("POST should not be allowed on /ping")
                .isIn(400, 404, 405);
    }

    // Test Case 3 - Random body sent to ping
    @Test(groups = {"negative"})
    @Story("Verify API Availability")
    @Description("Negative Test: Verify that sending a random body to GET /ping is handled gracefully and still returns 201.")
    @Severity(SeverityLevel.NORMAL)
    public void testPingWithRandomBody() {

        Response response = given(spec)
                .body("{ \"random\": \"data\" }")
                .when()
                .get("/ping");

        assertThat(response.statusCode()).isEqualTo(201);
    }
}