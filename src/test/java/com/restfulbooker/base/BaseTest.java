package com.restfulbooker.base;

import com.restfulbooker.utils.ConfigReader;
import com.restfulbooker.utils.TokenManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected RequestSpecification spec;
    protected RequestSpecification authSpec;
    protected static String token;
    @BeforeSuite(alwaysRun = true)
    public void globalSetUp(){
        // Get auth token ONCE for the entire suite
        token = TokenManager.getToken();
    }
    @BeforeClass(alwaysRun = true)
    public void setUp(){
        spec = getBaseSpecBuilder().build();

        authSpec = getBaseSpecBuilder()
                .addHeader("Cookie", "token=" + token)
                .build();
    }
    private RequestSpecBuilder getBaseSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .setRelaxedHTTPSValidation();
    }
}
