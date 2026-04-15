package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingTest extends BaseTest {

    private int bookingId;
    private BookingRequest payload;


    @BeforeClass
    public void createTestBooking(){
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-01-01" , "2025-01-15");
        payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");

        Response response = api.createBooking(payload);
        bookingId = response.jsonPath().getInt("bookingid");
    }

    // Test Case 1 — Valid ID returns 200 and correct data
    @Test
    public void testGetBookingByValidId(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    //Test Case 2 — All fields  what was sent during creation
    @Test
    public void testGetBookingDataMatchesCreation() {
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname"))
                .isEqualTo(payload.getFirstname());
        assertThat(response.jsonPath().getString("lastname"))
                .isEqualTo(payload.getLastname());
        assertThat(response.jsonPath().getInt("totalprice"))
                .isEqualTo(payload.getTotalprice());
        assertThat(response.jsonPath().getBoolean("depositpaid"))
                .isEqualTo(payload.isDepositpaid());
        assertThat(response.jsonPath().getString("bookingdates.checkin"))
                .isEqualTo(payload.getBookingdates().getCheckin());
        assertThat(response.jsonPath().getString("bookingdates.checkout"))
                .isEqualTo(payload.getBookingdates().getCheckout());
        assertThat(response.jsonPath().getString("additionalneeds"))
                .isEqualTo(payload.getAdditionalneeds());
    }

    // Test Case 3 — non-existing ID returns 404
    @Test
    public void testGetBookingByNonExistingId(){
        BookingRequests api = new BookingRequests(spec);
        int nonExistingId = 999999; // Assuming this ID does not exist
        Response response = api.getBooking(nonExistingId);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    // Test Case 4 — String ID returns 404
    @Test
    public void testGetBookingByStringId() {
        Response response = given(spec)
                .when().get("/booking/abc");
        assertThat(response.statusCode()).isEqualTo(404);
        System.out.println("the actual status code is : "+response.statusCode());
    }

    //Test Case 5 — Negative ID returns 404
    @Test
    public void testGetBookingByNegativeId() {
        int negativeId = -1;
        Response response = given(spec)
                .when().get("/booking/" + negativeId);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    //Test Case 6 — ID = 0 : should return 404
    @Test
    public void testGetBookingByZeroId() {
        int zeroId = 0;
        Response response = given(spec)
                .when().get("/booking/" + zeroId);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    //Test Case 7 — Very large ID : observe overflow behaviour
     @Test
    public void testGetBookingByVeryLargeId() {
        long veryLargeId = 9999999999L; // Assuming this ID does not exist
        Response response = given(spec)
                .when().get("/booking/" + veryLargeId);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    //Test Case 8 — Get a deleted Booking Id must return 404
    @Test
    public void testGetBookingByDeletedBookingId(){
        BookingRequests api = new BookingRequests(spec);
        BookingRequest tempPayload = new BookingRequest();
        tempPayload.setFirstname("Temp");
        tempPayload.setLastname("User");
        tempPayload.setTotalprice(50);
        tempPayload.setDepositpaid(false);
        tempPayload.setBookingdates(new BookingDates("2025-02-01", "2025-02-05"));
        tempPayload.setAdditionalneeds("none");

        Response createResponse = api.createBooking(tempPayload);
        int tempBookingId = createResponse.jsonPath().getInt("bookingid");
        api.deleteBooking(tempBookingId , token);
        Response response = api.getBooking(tempBookingId);

        assertThat(response.statusCode()).isEqualTo(404);
    }

}
