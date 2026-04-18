package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class UpdateBookingTest extends BaseTest {
    int bookingId;
    @BeforeClass
    public  void  createBooking(){
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
        System.out.println("Created booking with ID for PUT tests: " + bookingId);
    }

    //Test case 1 : valid token + all fields updated
    @Test
    public void testFullUpdateWithValidToken() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, token);
        response.then().statusCode(200);

        response.then().body("firstname", equalTo("James"));
        response.then().body("lastname", equalTo("Brown"));
        response.then().log().all().body("totalprice", equalTo(222));
        response.then().log().all().body("depositpaid", equalTo(false));
        response.then().log().all().body("bookingdates.checkin", equalTo("2025-08-01"));
        response.then().log().all().body("bookingdates.checkout", equalTo("2025-08-10"));
        response.then().log().all().body("additionalneeds", equalTo("Lunch"));

    }

    //Test case 2 : no token provided
    @Test
    public void testFullUpdateWithoutToken() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, null);
        response.then().statusCode(403);
    }

    //Test case 3 : wrong token provided
    @Test
    public void testFullUpdateWithWrongToken() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, "invalidtoken123");
        response.then().statusCode(403);
    }

    //Test case 4 : non-existent booking ID
    @Test
    public void testFullUpdateWithNonExistentBookingId() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        int nonExistentBookingId = 99999; // Assuming this ID does not exist
        Response response = api.updateBooking(nonExistentBookingId, payload, token);
        response.then().statusCode(405);
    }

    //Test case 5 : missing required field in the payload
    @Test
    public void testFullUpdateWithMissingRequiredField() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        // Missing firstname
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, token);
        response.then().statusCode(400);
    }

    //Test case 6 : negative total price
    @Test
    public void testFullUpdateWithNegativeTotalPrice() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-01", "2025-08-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(-100); // Invalid negative price
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, token);
        response.then().statusCode(400);
    }

    //Test case 7 : checkout before checkin
    @Test
    public void testFullUpdateWithCheckoutBeforeCheckin() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-08-10", "2025-08-01"); // Invalid dates
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("James");
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, token);
        response.then().statusCode(400);
    }
}
