package com.restfulbooker;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class HealthCheckTest {
    @BeforeClass
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }
    @Test
    public void testHealthCheck(){
       int statusCode = given()
               .when().get("/ping")
               .then()
               .extract().statusCode();
       assertEquals(statusCode, 201);
    }
    @Test
    public void testGetAllBookings(){
        Response response = RestAssured.given()
                .when().get("/booking")
                .then()
                .extract().response();
        assertEquals(response.statusCode() , 200);
        System.out.println("All Bookings IDs : "+ response.asString());
    }
    @Test
    public void testCreateBooking(){
        String body = """
                {
                       "firstname" : "Jim",
                       "lastname" : "Brown",
                       "totalprice" : 111,
                       "depositpaid" : true,
                       "bookingdates" : {
                           "checkin" : "2018-01-01",
                           "checkout" : "2019-01-01"
                       },
                       "additionalneeds" : "Breakfast"
                   }""";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when().post("/booking")
                .then()
                .extract().response();
        assertEquals(response.statusCode() , 200);
        System.out.println("Created Booking : "+ response.asString());
        int bookingId = response.jsonPath().getInt("bookingid");
        String firstName =response.jsonPath().getString("booking.firstname");
        assertEquals(firstName , "Jim");
        System.out.println("Booking ID : "+bookingId);
    }
    @Test
    public void testGetBookingById(){
        String body = """
                {
                       "firstname" : "Jim",
                       "lastname" : "Brown",
                       "totalprice" : 111,
                       "depositpaid" : true,
                       "bookingdates" : {
                           "checkin" : "2018-01-01",
                           "checkout" : "2019-01-01"
                       },
                       "additionalneeds" : "Breakfast"
                   } """;
        int bookingId = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when().post("/booking")
                .then()
                .extract().jsonPath().getInt("bookingid");
        Response getResponse= RestAssured.given()
                .when().get("/booking/"+bookingId)
                .then()
                .extract().response();
        assertEquals(getResponse.statusCode(), 200);
        String firstName =getResponse.jsonPath().getString("firstname");
        assertEquals(firstName , "Jim");
        System.out.println("Retrieved booking ID : "+ bookingId);
    }
}
