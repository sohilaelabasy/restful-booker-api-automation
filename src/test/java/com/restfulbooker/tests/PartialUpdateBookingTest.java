package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@Epic("Restful Booker API")
@Feature("Booking Management")
public class PartialUpdateBookingTest extends BaseTest {
    @Step("Setup: Creating a temporary booking for partial update")
    private int createBooking() {
        BookingRequests api = new BookingRequests(spec);

        BookingDates bookingDates = new BookingDates("2025-07-01", "2025-07-10");

        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");

        Response response = api.createBookingForSetup(payload);
        return response.jsonPath().getInt("bookingid");
    }

    //Test case 1 : update firstname only
    @Test(groups = { "smoke","regression"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Positive Test: Verify that updating only the firstname returns 200 OK and reflects the change.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateFirstNameOnly() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstname", "James");
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo("James");

        response.prettyPrint();
    }

    //Test case 2 : update lastname only
    @Test(groups = {"regression"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Positive Test: Verify that updating only the lastname returns 200 OK.")
    @Severity(SeverityLevel.NORMAL)
    public void updateLastNameOnly() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("lastname", "Smith");
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("lastname")).isEqualTo("Smith");
        response.prettyPrint();
    }

    //Test case 3 : update total price only
    @Test(groups = {"regression"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Positive Test: Verify that updating only the totalprice returns 200 OK.")
    @Severity(SeverityLevel.NORMAL)
    public void updateTotalPriceOnly() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("totalprice", 150);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(150);
        response.prettyPrint();
    }

    //Test case 4 : update multiple fields
    @Test(groups = {"smoke","regression"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Positive Test: Verify that updating multiple fields simultaneously returns 200 OK.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateMultipleFields() {
        int bookingId = createBooking();
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
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that partial update fails (403) without an authentication token.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateWithoutToken() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "smith");
        Response response = api.partialUpdateBooking(bookingId, payload, null);
        assertThat(response.getStatusCode()).isEqualTo(403);
        response.prettyPrint();
    }

    //Test case 6 : update with invalid token
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that partial update fails (403) with an invalid token.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateWithInvalidToken() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", "ahmed");
        Response response = api.partialUpdateBooking(bookingId, payload, "invalidtoken123");
        assertThat(response.getStatusCode()).isEqualTo(403);
        response.prettyPrint();
    }

    //Test case 7 : update with empty body
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that an empty body returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void updateWithEmptyBody() {
        int bookingId = createBooking();
        Response response = given(spec)
                .cookie("token" , token)
                .body("""
                        {}""")
                .when().patch("/booking/" + bookingId);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 8 : update with negative total price
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that negative totalprice returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void updateWithNegativeTotalPrice(){
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("totalprice", -50);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 9 : update with null firstname
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that null firstname returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void updateWithNullFirstName() {
        int bookingId = createBooking();
        BookingRequests api = new BookingRequests(spec);
        Map<String , Object> payload = new HashMap<>();
        payload.put("firstname", null);
        Response response = api.partialUpdateBooking(bookingId, payload, token);
        assertThat(response.getStatusCode()).isEqualTo(400);
        response.prettyPrint();
    }

    //Test case 10 : update with unknown field
    @Test(groups = {"negative"})
    @Story("Partial Update Booking (PATCH)")
    @Description("Negative Test: Verify that an unknown field results in a 404 or ignore behavior (based on API design).")
    @Severity(SeverityLevel.NORMAL)
    public void updateWithExtraField() {
        int bookingId = createBooking();
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
