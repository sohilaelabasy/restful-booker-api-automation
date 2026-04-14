package com.restfulbooker.base;

import com.restfulbooker.utils.ConfigReader;
import com.restfulbooker.utils.TokenManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected RequestSpecification spec;
    protected static String token;
    @BeforeSuite
    public void globalSetUp(){
        // Get auth token ONCE for the entire suite
        token = TokenManager.getToken();
    }
    @BeforeClass
    public void setUp(){
        spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.url"))
                .setContentType(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .build();
    }
}
