package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking Management")
public class UpdateBookingTest extends BaseTest {

    // 🔹 Helper: create booking and return ID
    @Step("Create a booking to be used in update tests")
    private int createBooking() {
        BookingRequests api = new BookingRequests(authSpec);

        BookingRequest payload = BookingRequest.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-07-01")
                                .checkout("2025-07-10")
                                .build()
                )
                .additionalneeds("Breakfast")
                .build();

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);

        int id = response.jsonPath().getInt("bookingid");
        assertThat(id).isGreaterThan(0);

        return id;
    }

    // ============================================================
    // Test case 1 : valid token + all fields updated
    // ============================================================

    @Test(groups = {"smoke","regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Positive Test: Verify that a full update with a valid token and all fields returns 200 OK.")
    @Severity(SeverityLevel.BLOCKER)
    public void testFullUpdateWithValidToken() {

        BookingRequests api = new BookingRequests(authSpec);

        int bookingId = createBooking();

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-01")
                                .checkout("2025-08-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Response response = api.updateBooking(bookingId, payload, token);

        assertThat(response.statusCode()).isEqualTo(200);

        var json = response.jsonPath();
        assertThat(json.getString("firstname")).as("Check Firstname").isEqualTo("James");
        assertThat(json.getString("lastname")).as("Check Lastname").isEqualTo("Brown");
        assertThat(json.getInt("totalprice")).as("Check Total Price").isEqualTo(222);
        assertThat(json.getBoolean("depositpaid")).as("Check Deposit Paid").isEqualTo(false);
        assertThat(json.getString("bookingdates.checkin")).as("Check Check-in Date").isEqualTo("2025-08-01");
        assertThat(json.getString("bookingdates.checkout")).as("Check Check-out Date").isEqualTo("2025-08-10");
        assertThat(json.getString("additionalneeds")).as("Check Additional Needs").isEqualTo("Lunch");
    }

    // ============================================================
    // Test case 2 : no token provided
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that a full update fails (403) when no authentication token is provided.")
    @Severity(SeverityLevel.CRITICAL)
    public void testFullUpdateWithoutToken() {

        BookingRequests api = new BookingRequests(spec);

        int bookingId = createBooking();

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-01")
                                .checkout("2025-08-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Response response = api.updateBooking(bookingId, payload, null);

        assertThat(response.statusCode()).isEqualTo(403);
    }

    // ============================================================
    // Test case 3 : wrong token provided
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that a full update fails (403) with an invalid authentication token.")
    @Severity(SeverityLevel.CRITICAL)
    public void testFullUpdateWithWrongToken() {

        BookingRequests api = new BookingRequests(spec);

        int bookingId = createBooking();

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-01")
                                .checkout("2025-08-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Response response = api.updateBooking(bookingId, payload, "invalidtoken123");

        assertThat(response.statusCode()).isEqualTo(403);
    }

    // ============================================================
    // Test case 4 : non-existent booking ID
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that updating a non-existent booking ID returns 405 Method Not Allowed.")
    @Severity(SeverityLevel.NORMAL)
    public void testFullUpdateWithNonExistentBookingId() {

        BookingRequests api = new BookingRequests(authSpec);

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-01")
                                .checkout("2025-08-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        int nonExistentBookingId = 99999999;

        Response response = api.updateBooking(nonExistentBookingId, payload, token);

        assertThat(response.statusCode()).isEqualTo(405);
    }

    // ============================================================
    // Test case 5 : missing required field in the payload
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that a full update with a missing required field returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void testFullUpdateWithMissingRequiredField() {

        BookingRequests api = new BookingRequests(authSpec);

        int bookingId = createBooking();

        BookingRequest payload = new BookingRequest(); // intentionally broken
        payload.setLastname("Brown");
        payload.setTotalprice(222);
        payload.setDepositpaid(false);
        payload.setBookingdates(new BookingDates("2025-08-01", "2025-08-10"));
        payload.setAdditionalneeds("Lunch");

        Response response = api.updateBooking(bookingId, payload, token);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // ============================================================
    // Test case 6 : negative total price
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that a full update with a negative totalprice returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void testFullUpdateWithNegativeTotalPrice() {

        BookingRequests api = new BookingRequests(authSpec);

        int bookingId = createBooking();

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(-100)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-01")
                                .checkout("2025-08-10")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Response response = api.updateBooking(bookingId, payload, token);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    // ============================================================
    // Test case 7 : checkout before checkin
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Full Update Booking (PUT)")
    @Description("Negative Test: Verify that a full update with checkout before checkin returns 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void testFullUpdateWithCheckoutBeforeCheckin() {

        BookingRequests api = new BookingRequests(authSpec);

        int bookingId = createBooking();

        BookingRequest payload = BookingRequest.builder()
                .firstname("James")
                .lastname("Brown")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-08-10")
                                .checkout("2025-08-01")
                                .build()
                )
                .additionalneeds("Lunch")
                .build();

        Response response = api.updateBooking(bookingId, payload, token);

        assertThat(response.statusCode()).isEqualTo(400);
    }
}