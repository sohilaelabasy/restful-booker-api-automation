package com.restfulbooker.utils;
import io.restassured.response.Response;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class TokenManager {
    public static String getToken(){
        String body = """
                {
                    "username" : "%s",
                    "password" : "%s"
                }  """.formatted(
                        ConfigReader.get("auth.username"),
                ConfigReader.get("auth.password")
        );
        Response response = given()
                .contentType("application/json")
                .body(body)
                .when().post(ConfigReader.get(baseURI)+"/auth")
                .then()
                .extract().response();
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get token. Status code: " + response.statusCode());
        }
        return response.jsonPath().getString("token");
    }
}
