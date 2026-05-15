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
public class GetBookingTest extends BaseTest {

    // 🔹 Helper: create booking + return (id + payload)
    @Step("Create booking for GET tests")
    private Object[] createBooking() {

        BookingRequests api = new BookingRequests(authSpec);

        BookingRequest payload = BookingRequest.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(
                        BookingDates.builder()
                                .checkin("2025-01-01")
                                .checkout("2025-01-15")
                                .build()
                )
                .additionalneeds("Breakfast")
                .build();

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);

        int id = response.jsonPath().getInt("bookingid");
        assertThat(id).isGreaterThan(0);

        return new Object[]{id, payload};
    }

    // ============================================================
    // Test Case 1 — Valid ID returns 200
    // ============================================================

    @Test(groups = {"regression", "smoke"})
    @Story("Retrieve booking by valid ID")
    @Description("Positive Test: Verify that GET /booking/{id} returns 200 OK for a valid existing ID.")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetBookingByValidId() {

        BookingRequests api = new BookingRequests(spec);

        int bookingId = (int) createBooking()[0];

        Response response = api.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(200);
    }

    // ============================================================
    // Test Case 2 — Data matches creation
    // ============================================================

    @Test(groups = {"regression"})
    @Story("Retrieve booking by valid ID")
    @Description("Positive Test: Verify that the retrieved booking data matches exactly what was sent during creation.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookingDataMatchesCreation() {

        BookingRequests api = new BookingRequests(spec);

        Object[] data = createBooking();
        int bookingId = (int) data[0];
        BookingRequest payload = (BookingRequest) data[1];

        Response response = api.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("firstname")).isEqualTo(payload.getFirstname());
        assertThat(response.jsonPath().getString("lastname")).isEqualTo(payload.getLastname());
        assertThat(response.jsonPath().getInt("totalprice")).isEqualTo(payload.getTotalprice());
        assertThat(response.jsonPath().getBoolean("depositpaid")).isEqualTo(payload.isDepositpaid());
        assertThat(response.jsonPath().getString("bookingdates.checkin"))
                .isEqualTo(payload.getBookingdates().getCheckin());
        assertThat(response.jsonPath().getString("bookingdates.checkout"))
                .isEqualTo(payload.getBookingdates().getCheckout());
        assertThat(response.jsonPath().getString("additionalneeds"))
                .isEqualTo(payload.getAdditionalneeds());
    }

    // ============================================================
    // Test Case 3 — non-existing ID
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByNonExistingId() {

        BookingRequests api = new BookingRequests(spec);

        Response response = api.getBooking(99999999);

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ============================================================
    // Test Case 4 — String ID
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByStringId() {

        Response response = given(spec)
                .when().get("/booking/abc");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ============================================================
    // Test Case 5 — Negative ID
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByNegativeId() {

        Response response = given(spec)
                .when().get("/booking/-1");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ============================================================
    // Test Case 6 — Zero ID
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByZeroId() {

        Response response = given(spec)
                .when().get("/booking/0");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ============================================================
    // Test Case 7 — Very large ID
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByVeryLargeId() {

        Response response = given(spec)
                .when().get("/booking/9999999999");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    // ============================================================
    // Test Case 8 — Deleted booking
    // ============================================================

    @Test(groups = {"regression"})
    public void testGetBookingByDeletedBookingId() {

        BookingRequests api = new BookingRequests(authSpec);

        int bookingId = (int) createBooking()[0];

        api.deleteBooking(bookingId, token);

        Response response = api.getBooking(bookingId);

        assertThat(response.statusCode()).isEqualTo(404);
    }
}