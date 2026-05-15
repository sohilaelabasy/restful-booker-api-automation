package com.restfulbooker.utils;
import io.restassured.response.Response;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class TokenManager {
    public static String getToken(){
        Response response = given()
                .baseUri(ConfigReader.get("base.url"))
                .contentType("application/json")
                .body("{ \"username\": \""+ConfigReader.get("auth.username")+"\", \"password\": \""
                        +ConfigReader.get("auth.password")+"\" }")
                .when().post("/auth")
                .then()
                .extract().response();
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get token. Status code: " + response.statusCode());
        }
        return response.jsonPath().getString("token");
    }
}
