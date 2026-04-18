package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PartialUpdateBooking  extends BaseTest {
    int bookingId;
    @BeforeClass
    public void createBooking(){
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-07-01", "2025-07-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");
        Response response = api.createBooking(payload);
        bookingId = response.jsonPath().getInt("bookingid");
        System.out.println("Created booking with ID for PATCH tests: " + bookingId);

    }

    //Test case 1 : update firstname only
    @Test
    public void updateFirstNameOnly() {
        BookingRequests api = new BookingRequests(spec);
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstname", "James");
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo("James");

        response.prettyPrint();


    }

    //Test case 2 : update lastname only
    @Test
    public void updateLastNameOnly() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("lastname", "Smith");
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("lastname")).isEqualTo("Smith");
        response.prettyPrint();
    }

    //Test case 3 : update total price only
    @Test
    public void updateTotalPriceOnly() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("totalprice", 150);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(150);
        response.prettyPrint();
    }

    //Test case 4 : update multiple fields
    @Test
    public void updateMultipleFields() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "Michael");
        payload.put("lastname", "Johnson");
        payload.put("totalprice", 200);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo("Michael");
        assertThat(response.jsonPath().getString("lastname")).isEqualTo("Johnson");
        assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(200);
        response.prettyPrint();
    }

    //Test case 5 : update with no token
    @Test
    public void updateWithoutToken() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "smith");
        Response response = api.partialUpdateBooking(bookingId, payload, null);
        assertThat(response.getStatusCode()).isEqualTo(403);
        response.prettyPrint();
    }

    //Test case 6 : update with invalid token
    @Test
    public void updateWithInvalidToken() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "ahmed");
        Response response = api.partialUpdateBooking(bookingId, payload, "invalidtoken123");
        assertThat(response.getStatusCode()).isEqualTo(403);
        response.prettyPrint();
    }

    //Test case 7 : update with empty body
    @Test
    public void updateWithEmptyBody() {
        Response response = given(spec)
                .cookie("token" , token)
                .body("""
                        {}""")
                .when().patch("/booking/" + bookingId);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 8 : update with negative total price
    @Test
    public void updateWithNegativeTotalPrice(){
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("totalprice", -50);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 9 : update with null firstname
    @Test
    public void updateWithNullFirstName() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", null);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 10 : update with unknown field
    @Test
    public void updateWithExtraField() {
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "David");
        payload.put("unknown field", "extra value");
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo("David");
        response.prettyPrint();
    }
}
