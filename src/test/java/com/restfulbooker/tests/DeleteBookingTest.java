package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBookingTest extends BaseTest {
    private int createBooking(){
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload =new BookingRequest();
        BookingDates bookingDates = new BookingDates("2025-01-01" , "2025-01-15");

        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");

        Response response = api.createBooking(payload);
        return response.jsonPath().getInt("bookingid");
    }

    //Test case 1 - Valid ID deletion returns 201
    @Test
    public void testDeleteBookingWithValidId(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , token);
        assertThat(response.statusCode()).isEqualTo(201);
    }

    //Test case 2 - No token is provided
    @Test
    public void testDeleteBookingWithoutToken(){
        Response response = given(spec)
                .when().delete("/booking/" + createBooking());
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 3 - delete already-deleted booking
    @Test
    public void testDeleteBookingWithDeletedId(){
        BookingRequests api = new BookingRequests(spec);
        int id = createBooking();
        api.deleteBooking(id , token);// First Deletion
        Response response = api.deleteBooking(id,token); // second Deletion

        assertThat(response.statusCode()).isIn(404 , 405);
    }

    // Test Case 4— Delete non-existent ID returns 405
    @Test
    public void testDeleteBookingWithNonExistingId(){
        BookingRequests api = new BookingRequests(spec);

        Response response = api.deleteBooking(999999 , token);
        assertThat(response.statusCode()).isEqualTo(405);
    }

    //Test case 5 - Empty token
    @Test
    public void testDeleteBookingWithEmptyToken(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , "");
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 6 - delete with numeric token
    @Test
    public void testDeleteBookingWithNumericToken(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , "12345");
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 7 - delete negative id
    @Test
    public void testDeleteBookingWithNegativeId(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(-102054 , token);
        assertThat(response.statusCode()).isNotEqualTo(500);
        System.out.println(response.statusCode());

    }

}
