package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.utils.ConfigReader;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
public class AuthTests extends BaseTest {
        // Test Case 1 — Valid Credentials
    @Test
    public void testValidCredentialsReturns200() {
        Response response = given(spec)
                .body("{ \"username\": \""+ ConfigReader.get("auth.username")+"\", \"password\": \""+ConfigReader.get("auth.password")+"\" }")
                .when().post("/auth");
        assertThat(response.statusCode()).isEqualTo(200);
    }
        // Test Case 2 — Invalid Credentials
    @Test
    public void testValidCredentialsReturnNonEmptyToken(){
        Response response = given(spec)
                .body("{ \"username\": \""+ ConfigReader.get("auth.username")+"\", \"password\": \""+ConfigReader.get("auth.password")+"\" }")
                .when().post("/auth");

        String token = response.jsonPath().getString("token");
        assertThat(token).isNotNull();
        assertThat(token.length()).isGreaterThan(0);
    }
        // Test Case 3 — wrong Username
    @Test
    public void testWrongUsernameReturnsBadCredentials(){
        Response response = given(spec)
                .body("{ \"username\": \"wronguser\", \"password\": \""+ConfigReader.get("auth.password")+"\" }")
                .when().post("/auth");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 3 — wrong password
    @Test
    public void testWrongPasswordReturnsBadCredentials() {
        Response response = given(spec)
                .body("{\"username\": \""+ConfigReader.get("auth.username")+"\" , \"password\": \"wrongpass\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }

    // Test Case 5 — Empty Username
    @Test
    public void testEmptyUsernameReturnsBadCredentials(){
        Response response = given(spec)
                .body("{ \"username\": \"\", \"password\": \""+ ConfigReader.get("auth.password") +"\" }")
                .when().post("/auth");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }
        // Test Case 6—Empty body
        @Test
        public void testEmptyBodyDoesNotReturnToken() {
            Response response = given(spec)
                    .body("{}")
                    .when().post("/auth");

            assertThat(response.statusCode()).isEqualTo(200);
            String token = response.jsonPath().getString("token");
            assertThat(token).isNull();
        }
        //Test Case 7 — SQL injection in username
        @Test
    public void testSQLInjectionInUsername(){
            Response response = given(spec)
                    .body("{ \"username\": \"admin'--\", \"password\": \"password123\" }")
                    .when().post("/auth");

            System.out.println("SQL injection status: " + response.statusCode());
            System.out.println("SQL injection body: " + response.asString());

            // Must return Bad credentials, not a token
            assertThat(response.jsonPath().getString("reason"))
                    .isEqualTo("Bad credentials");
        }
            //Test Case 8 — Very long username
        @Test
    public void testVeryLongUsername(){
        String longUsername = "user".repeat(100); // 4000 characters
            Response response = given(spec)
                    .body("{ \"username\": \"" + longUsername + "\", \"password\": \"password123\" }")
                    .when().post("/auth");

            System.out.println("Long username status: " + response.statusCode());
            // API must not return 500
            assertThat(response.statusCode()).isNotEqualTo(500);
        }
        //Test Case 9 — Special characters in password
        @Test
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
        //Test Case 10 — No Content-Type header
    @Test
    public void testNoContentTypeHeader() {
        Response response = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .body("{ \"username\": \""+ ConfigReader.get("auth.username")+"\", \"password\": \""+ConfigReader.get("auth.password")+"\" }")
                .when().post("/auth");
       assertThat(response.statusCode()).isNotEqualTo(500);
    }
    //Test Case 11 — Username as integer
    @Test
    public void testUsernameAsInteger() {
        Response response = given(spec)
                .body("{ \"username\": 12345, \"password\": \"" + ConfigReader.get("auth.password") + "\" }")
                .when().post("/auth");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("reason"))
                .isEqualTo("Bad credentials");
    }
    //test case 12
}
