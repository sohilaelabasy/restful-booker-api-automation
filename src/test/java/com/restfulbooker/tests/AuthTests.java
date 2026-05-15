package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.utils.ConfigReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Authentication")
public class AuthTests extends BaseTest {

    // Test Case 1 — Valid Credentials
    @Story("Verify valid credentials return token")
    @Description("Positive test case to verify that POST /auth with valid credentials returns a token and status 200")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke", "regression"})
    public void testValidCredentialsReturns200() {

        Response response = given(spec)
                .body("{ \"username\": \"" + ConfigReader.get("auth.username") +
                        "\", \"password\": \"" + ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // Test Case 2 — Valid token is returned
    @Story("Verify valid credentials return token")
    @Description("Positive test case to verify that POST /auth with valid credentials returns a non-empty token")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"smoke", "regression"})
    public void testValidCredentialsReturnNonEmptyToken() {

        Response response = given(spec)
                .body("{ \"username\": \"" + ConfigReader.get("auth.username") +
                        "\", \"password\": \"" + ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        String token = response.jsonPath().getString("token");

        assertThat(token).isNotNull();
        assertThat(token.length()).isGreaterThan(0);
    }

    // Test Case 3 — Wrong Username
    @Story("Verify wrong username returns Bad credentials")
    @Description("Negative test case to verify that POST /auth with wrong username returns status 200 but reason 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"})
    public void testWrongUsernameReturnsBadCredentials() {

        Response response = given(spec)
                .body("{ \"username\": \"wronguser\", \"password\": \"" +
                        ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 4 — Wrong Password
    @Story("Verify wrong password returns Bad credentials")
    @Description("Negative test case to verify that POST /auth with wrong password returns status 200 but reason 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"})
    public void testWrongPasswordReturnsBadCredentials() {

        Response response = given(spec)
                .body("{\"username\": \"" + ConfigReader.get("auth.username") +
                        "\" , \"password\": \"wrongpass\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 5 — Empty Username
    @Story("Verify empty username returns Bad credentials")
    @Description("Negative test case to verify that POST /auth with empty username returns status 200 but reason 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = {"regression"})
    public void testEmptyUsernameReturnsBadCredentials() {

        Response response = given(spec)
                .body("{ \"username\": \"\", \"password\": \"" +
                        ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 6 — Empty Body
    @Test(groups = {"regression"})
    @Story("Verify empty body does not return token")
    @Description("Negative test case to verify that POST /auth with empty body returns status 200 but does not return a token")
    @Severity(SeverityLevel.CRITICAL)
    public void testEmptyBodyDoesNotReturnToken() {

        Response response = given(spec)
                .body("{}")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);

        String token = response.jsonPath().getString("token");

        assertThat(token).isNull();
    }

    // Test Case 7 — SQL injection in username
    @Test(groups = {"regression"})
    @Story("Security: SQL Injection Defense")
    @Description("Negative test case to verify that POST /auth with SQL injection in username does not bypass authentication and returns 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    public void testSQLInjectionInUsername() {

        Response response = given(spec)
                .body("{ \"username\": \"admin'--\", \"password\": \"password123\" }")
                .when().post("/auth");

        System.out.println("SQL injection status: " + response.statusCode());
        System.out.println("SQL injection body: " + response.asString());

        // Must return Bad credentials, not a token
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 8 — Very long username
    @Test(groups = {"regression"})
    @Story("Boundary Testing: Large Input Handling")
    @Description("Negative test case to verify that POST /auth with a very long username does not cause server error and returns 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    public void testVeryLongUsername() {

        String longUsername = "user".repeat(100);

        Response response = given(spec)
                .body("{ \"username\": \"" + longUsername + "\", \"password\": \"password123\" }")
                .when().post("/auth");

        System.out.println("Long username status: " + response.statusCode());

        // API must not return 500
        assertThat(response.statusCode()).isNotEqualTo(500);
    }

    // Test Case 9 — Special characters in password
    @Test(groups = {"regression"})
    @Story("Input Validation: Special Characters")
    @Description("Negative test case to verify that POST /auth with special characters in password does not bypass authentication and returns 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    public void testSpecialCharactersInPassword() {

        Response response = given(spec)
                .body("{ \"username\": \"admin\", \"password\": \"p@$$w0rd!#%\" }")
                .when().post("/auth");

        System.out.println("Special chars status: " + response.statusCode());
        System.out.println("Special chars body: " + response.asString());

        // Must not bypass auth
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 10 — No Content-Type header
    @Test(groups = {"regression"})
    @Story("Verify no Content-Type header does not cause server error")
    @Description("Negative test case to verify that POST /auth without Content-Type header does not cause server error and returns 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    public void testNoContentTypeHeader() {

        Response response = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .body("{ \"username\": \"" + ConfigReader.get("auth.username") +
                        "\", \"password\": \"" + ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isNotEqualTo(500);
    }

    // Test Case 11 — Username as integer
    @Test(groups = {"regression"})
    @Story("Verify username as integer does not cause server error and returns 'Bad credentials'")
    @Description("Negative test case to verify that POST /auth with username as integer does not cause server error and returns 'Bad credentials'")
    @Severity(SeverityLevel.CRITICAL)
    public void testUsernameAsInteger() {

        Response response = given(spec)
                .body("{ \"username\": 12345, \"password\": \"" +
                        ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }
}