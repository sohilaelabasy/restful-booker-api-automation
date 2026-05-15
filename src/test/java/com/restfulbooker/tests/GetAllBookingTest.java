package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;

import com.restfulbooker.requests.BookingRequests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking Management")
public class GetAllBookingTest extends BaseTest {
    // Test Case 1 — Response status is 200
    @Test(groups = {"smoke","regression"})
    @Story("Retrieve All Bookings")
    @Description("Positive Test: Verify that GET /booking returns 200 OK .")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllBookingsReturns200(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getAllBookings();
        assertThat(response.statusCode(), is(200));
    }

    // Test Case 2 — Response body is a JSON array
    @Test(groups = {"regression"})
    @Story("Retrieve All Bookings")
    @Description("Positive Test: Verify that GET /booking returns a valid JSON array of booking IDs.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllBookingsReturnsJsonArray(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getAllBookings();
        List<Map<String , Object>> bookings = response.jsonPath().getList("$");
        assertThat(bookings).isNotNull();
    }

    // Test Case 3 — Each item in array has a 'bookingid' integer field
    @Test(groups = {"regression"})
    @Story("Retrieve All Bookings")
    @Description("Positive Test: Verify that each item in the array contains a valid 'bookingid' integer field.")
    @Severity(SeverityLevel.NORMAL)
    public void testEachBookingHasBookingIdField(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getAllBookings();
        List<Map<String , Object>> bookings = response.jsonPath().getList("$");
        for(Map<String , Object>booking:bookings){
            assertThat(booking.get("bookingid"))
                    .as("bookingid should not be null")
                    .isNotNull();
            assertThat((int) booking.get("bookingid"))
                    .as("bookingid should be an integer greater than 0")
                    .isGreaterThan(0);
        }

    }

    // Test Case 4 — Array is not empty
    @Test(groups = {"regression"})
    @Story("Retrieve All Bookings")
    @Description("Positive Test: Verify that the bookings list is not empty.")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllBookingsListIsNotEmpty(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.getAllBookings();
        List<Map<String , Object>> bookings = response.jsonPath().getList("$");
        assertThat(bookings).isNotEmpty();
    }

    //Test case 5 -Wrong Accept header
    @Test(groups = {"regression"})
    @Story("Retrieve All Bookings")
    @Description("Negative Test: Verify that sending a wrong Accept header (text/html) does not cause a server crash (500).")
    @Severity(SeverityLevel.MINOR)
    public void testGetAllBookingsWithWrongAcceptHeader() {
        Response response = given(spec)
                .header("Accept", "text/html")
                .when().get("/booking");

        // API must still return JSON or reject cleanly — must not crash
        assertThat(response.statusCode()).isNotEqualTo(500);
    }

    //Test case 6 -  Unknown query param
    @Test(groups = {"regression"})
    @Story("Retrieve All Bookings")
    @Description("Negative Test: Verify that an unknown query parameter is ignored and the API returns 200 OK.")
    @Severity(SeverityLevel.MINOR)
    public void testGetAllBookingsUnknownQueryParamIsIgnored() {
        Response response = given(spec)
                .queryParam("random", "xyz")
                .when().get("/booking");

        System.out.println("Unknown param status: " + response.statusCode());
        assertThat(response.statusCode()).isEqualTo(200);

        List<Map<String, Object>> bookings = response.jsonPath().getList("$");
        assertThat(bookings).isNotEmpty();
    }
}
