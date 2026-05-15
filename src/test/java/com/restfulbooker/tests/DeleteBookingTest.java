package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking Management")
public class DeleteBookingTest extends BaseTest {
    @Step("Setup: Creating a temporary booking for test")
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
    @Test(groups = {"regression" , "smoke"})
    @Story("Delete booking with valid token and valid id")
    @Description("Positive Test: Verify that DELETE /booking/{id} returns 201 when a valid token and existing booking id are provided")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBookingWithValidId(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , token);
        assertThat(response.statusCode()).isEqualTo(201);
    }

    //Test case 2 - No token is provided
    @Test(groups = {"regression"})
    @Story("Reject deletion without authentication token")
    @Description("Negative Test: Verify that DELETE /booking/{id} returns 403 Forbidden when no authentication token is provided")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBookingWithoutToken(){
        Response response = given(spec)
                .when().delete("/booking/" + createBooking());
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 3 - delete already-deleted booking
    @Test(groups = {"regression"})
    @Story("Reject deletion of already deleted booking")
    @Description("Negative Test: Verify that deleting the same booking twice returns 404 or 405 on the second attempt")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithDeletedId(){
        BookingRequests api = new BookingRequests(spec);
        int id = createBooking();
        api.deleteBooking(id , token);// First Deletion
        Response response = api.deleteBooking(id,token); // second Deletion

        assertThat(response.statusCode()).isIn(404 , 405);
    }

    // Test Case 4— Delete non-existent ID returns 405
    @Test(groups = {"regression"})
    @Story("Reject deletion of non-existing booking id")
    @Description("Negative Test: Verify that DELETE /booking/{id} returns 405 when the booking id does not exist")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithNonExistingId(){
        BookingRequests api = new BookingRequests(spec);

        Response response = api.deleteBooking(999999 , token);
        assertThat(response.statusCode()).isEqualTo(405);
    }

    //Test case 5 - Empty token
    @Test(groups = {"regression"})
    @Story("Reject deletion with empty token")
    @Description("Negative Test: Verify that DELETE /booking/{id} returns 403 when an empty authentication token is sent")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithEmptyToken(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , "");
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 6 - delete with numeric token
    @Test(groups = {"regression"})
    @Story("Reject deletion with invalid numeric token")
    @Description("Negative Test: Verify that DELETE /booking/{id} returns 403 when an invalid numeric token is provided")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithNumericToken(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(createBooking() , "12345");
        assertThat(response.statusCode()).isEqualTo(403);
    }

    //Test case 7 - delete negative id
    @Test(groups = {"regression"})
    @Story("Handle negative booking id gracefully")
    @Description("Negative Test: Verify that DELETE /booking/{id} with a negative id does not crash the API with status code 500")
    @Severity(SeverityLevel.MINOR)
    public void testDeleteBookingWithNegativeId(){
        BookingRequests api = new BookingRequests(spec);
        Response response = api.deleteBooking(-102054 , token);
        assertThat(response.statusCode()).isNotEqualTo(500);
        System.out.println(response.statusCode());

    }

}
