package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Search and Filter")
public class FilterBookingTest extends BaseTest {

    @Test(groups = {"regression"})
    @Story("Filter bookings by first name")
    @Description("Positive test case : To Verify that the API returns bookings matching the provided firstname")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByFirstname() {
        Response response = given(spec)
                .queryParam("firstname", "Jim")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
    }

    @Test(groups = {"regression"})
    @Story("Filter bookings by last name")
    @Description("Positive test case : To Verify that the API returns bookings matching the provided lastname")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByLastname() {
        Response response = given(spec)
                .queryParam("lastname", "Brown")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
    }

    @Test(groups = {"regression"})
    @Story("Filter bookings by check-in date")
    @Description("Positive test case : To Verify that the API returns bookings with check-in dates greater than or equal to the provided date")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByCheckinDate() {
        String filterDate = "2025-07-01";
        Response response = given(spec)
                .queryParam("checkin", filterDate)
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
    }

    @Test(groups = {"regression"})
    @Story("Filter bookings by check-out date")
    @Description("Positive test case : To Verify that the API returns bookings with check-out dates less than or equal to the provided date")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByCheckout() {
        String filterDate = "2024-01-01";
        Response response = given(spec)
                .queryParam("checkout", filterDate)
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
    }

    @Test(groups = {"regression"})
    @Story("Filter by check-in and check-out range")
    @Description("Positive test case : To Verify the logical consistency of results when filtering by a date range")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByCheckinAndCheckout() {
        String checkinDate = "2014-03-13";
        String checkoutDate = "2014-05-21";

        Response response = given(spec)
                .queryParam("checkin", checkinDate)
                .queryParam("checkout", checkoutDate)
                .when().get("/booking");

        List<Integer> ids = response.jsonPath().getList("bookingid");
        assertThat(response.statusCode()).isEqualTo(200);

        for (Integer id : ids) {
            Response booking = given(spec)
                    .pathParam("id", id)
                    .when().get("/booking/{id}");

            String actualCheckin = booking.jsonPath().getString("bookingdates.checkin");
            String actualCheckout = booking.jsonPath().getString("bookingdates.checkout");

            assertThat(actualCheckin.compareTo(checkinDate) >= 0).isTrue();
            assertThat(actualCheckout.compareTo(checkoutDate) >= 0).isTrue();
        }
    }

    @Test(groups = {"regression"})
    @Story("Filter with no matching results")
    @Description("Negative test case : ToVerify that an empty list is returned when no bookings match the filter criteria")
    @Severity(SeverityLevel.MINOR)
    public void testFilterWithNoMatchingResults() {
        Response response = given(spec)
                .queryParam("firstname", "ggsgghaajjwjw")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isEmpty();
    }

    @Test(groups = {"regression"})
    @Story("Filter with invalid date format")
    @Description("Negative test case: Verify API returns 400 when an invalid date format is used in filters")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterWithInvalidDateFormat() {
        Response response = given(spec)
                .queryParam("checkin", "not-a-date")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test(groups = {"regression"})
    @Story("Security: SQL Injection Defense")
    @Description("Negative test case : To Verify that SQL injection patterns in query parameters are handled safely")
    @Severity(SeverityLevel.CRITICAL)
    public void testFilterWithSQLInjectionInFirstname() {
        Response response = given(spec)
                .queryParam("firstname", "Jim' OR '1' = '1'")
                .when().get("/booking");
        assertThat(response.statusCode()).isIn(400, 200);
    }

    @Test(groups = {"regression"})
    @Story("Boundary Testing: Large Filter Value")
    @Description("Verify that very long string values in query parameters do not crash the server")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterWithTooLongValue() {
        Response response = given(spec)
                .queryParam("firstname", "A".repeat(500))
                .when().get("/booking");
        assertThat(response.statusCode()).isNotEqualTo(500);
    }
}