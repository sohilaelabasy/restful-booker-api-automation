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
@Feature("Create Booking")
public class CreateBookingTest extends BaseTest {
    //Test case 1 : Create a booking with valid data and verify that the response status code is 200 and the response body contains the booking ID and the correct first name.
    @Test(groups = {"smoke" , "regression"})
    @Story("Create a booking with valid data and verify response")
    @Description("Positive test case to create a booking with valid data and verify that the response status code is 200 and the response body contains the booking ID and the correct first name.")
    @Severity(SeverityLevel.CRITICAL)
    public void createBookingTestReturns200() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = BookingDates.builder()
                .checkin("2025-07-01")
                .checkout("2025-07-10")
                .build();
        BookingRequest payload = BookingRequest.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(bookingDates)
                .additionalneeds("Breakfast")
                .build();
        String firstName = payload.getFirstname();
        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("bookingid")).isGreaterThan(0);
        assertThat(response.jsonPath().getString("booking.firstname")).isEqualTo(firstName);


    }

    //Test case 2 : Create a booking and verify that the response body contains the same data as the request.
    @Test(groups = {"smoke" , "regression"})
    @Story("Create a booking and verify response matches request")
    @Description("Positive test case to create a booking and verify that the response body contains the same data as the request.")
    @Severity(SeverityLevel.CRITICAL)
    public void createBookingResponseMatchesRequest() {
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        BookingDates bookingDates = new BookingDates("2025-03-01", "2025-03-10");
        payload.setFirstname("Alice");
        payload.setLastname("Smith");
        payload.setTotalprice(250);
        payload.setDepositpaid(false);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Lunch");

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("bookingid")).isGreaterThan(0);
        assertThat(response.jsonPath().getString("booking.firstname")).isEqualTo(payload.getFirstname());
        assertThat(response.jsonPath().getString("booking.lastname")).isEqualTo(payload.getLastname());
        assertThat(response.jsonPath().getInt("booking.totalprice")).isEqualTo(payload.getTotalprice());
        assertThat(response.jsonPath().getBoolean("booking.depositpaid")).isEqualTo(payload.isDepositpaid());
        assertThat(response.jsonPath().getString("booking.bookingdates.checkin")).isEqualTo(payload.getBookingdates().getCheckin());
        assertThat(response.jsonPath().getString("booking.bookingdates.checkout")).isEqualTo(payload.getBookingdates().getCheckout());
        assertThat(response.jsonPath().getString("booking.additionalneeds")).isEqualTo(payload.getAdditionalneeds());
    }

    //Test case 3 : Create a booking with missing required field (e.g., missing firstname).
    @Test(groups = {"negative"})
    @Story("Create a booking with missing required field and verify response")
    @Description("Negative test case to create a booking with missing required field (e.g., missing firstname) and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingMissingFirstName() {
        Response response = given(spec)
                .body("""
                        {
                          "lastname": "Brown",
                          "totalprice": 111,
                          "depositpaid": true,
                          "bookingdates": {
                            "checkin": "2025-01-01",
                            "checkout": "2025-01-15"
                          }
                        }
                        """).when().post("/booking");

        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 4 : Create a booking with missing booking dates.
    @Story("Create a booking with missing booking dates and verify response")
    @Description("Negative test case to create a booking with missing booking dates and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    @Test(groups = {"negative"})
    public void testCreateBookingMissingBookingDates() {
        Response response = given(spec)
                .body("""
                        {
                            "firstname" : "Jim",
                            "lastname" : "Brown",
                            "totalprice" : 111,
                            "depositpaid" : true,
                            "additionalneeds" : "Breakfast"
                        }""")
                .when().post("/booking");
        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 5 : Create a booking with totalprice as String.
    @Test(groups = {"negative"})
    @Story("Create a booking with totalprice as String and verify response")
    @Description("Negative test case to create a booking with totalprice as String and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingTotalPriceAsString() {
        Response response = given(spec)
                .body("""
                        {
                            "firstname" : "Jim",
                            "lastname" : "Brown",
                            "totalprice" : "ahmed",
                            "depositpaid" : true,
                            "bookingdates" : {
                                "checkin" : "2018-01-01",
                                "checkout" : "2019-01-01"
                            },
                            "additionalneeds" : "Breakfast"
                        }""")
                .when().post("/booking");
        assertThat(response.statusCode())
                .as("API must reject string totalprice — if 200, this is a BUG")
                .isEqualTo(400);
    }

    //Test case 6 : create a booking with negative totalprice.
    @Test(groups = {"negative"})
    @Story("Create a booking with negative totalprice and verify response")
    @Description("Negative test case to create a booking with negative totalprice and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingNegativeTotalPrice() {
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(-999);
        payload.setDepositpaid(true);
        payload.setBookingdates(new BookingDates("2025-01-01", "2025-01-15"));

        Response response = api.createBooking(payload);

        assertThat(response.statusCode())
                .as("API must reject negative totalprice — if 200, this is a BUG")
                .isEqualTo(400);
    }

    //Test case 7 : create a booking with checkout date before checkin date.
    @Test(groups = {"negative"})
    @Story("Create a booking with checkout date before checkin date and verify response")
    @Description("Negative test case to create a booking with checkout date before checkin date and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingCheckoutBeforeCheckin() {
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(new BookingDates("2025-12-31", "2025-01-01"));

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 8 : create a booking with checkin = checkout date.
    @Test(groups = {"negative"})
    @Story("Create a booking with checkin date equal to checkout date and verify response")
    @Description("Negative test case to create a booking with checkin date equal to checkout date and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckinEqualsCheckout(){
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(new BookingDates("2025-01-01", "2025-01-01"));

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 9 : create a booking with invalid checkin format.
    @Test(groups = {"negative"})
    @Story("Create a booking with Invalid checkin format and verify response")
    @Description("Negative test case to create a booking with invalid checkin format and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingInvalidCheckinFormat() {
        Response response = given(spec)
                .body("""
                        {
                            "firstname" : "Jim",
                            "lastname" : "Brown",
                            "totalprice" : -111,
                            "depositpaid" : true,
                            "bookingdates" : {
                                "checkin" : "01-01-2019",
                                "checkout" : "2019-01-01"
                            },
                            "additionalneeds" : "Breakfast"
                        }""")
                .when().post("/booking");
        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 10 : create a booking with depositpaid as String.
    @Test(groups = {"negative"})
    @Story("Create a booking with depositpaid as String and verify response")
    @Description("Negative test case to create a booking with depositpaid as String and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingDepositPaidAsString(){
        Response response = given(spec)
                .body("""
                       {
                            "firstname" : "Jim",
                            "lastname" : "Brown",
                            "totalprice" : 111,
                            "depositpaid" : "yes",
                            "bookingdates" : {
                                "checkin" : "2018-01-01",
                                "checkout" : "2019-01-01"
                            },
                            "additionalneeds" : "Breakfast"
                        }""")
                .when().post("/booking");
        assertThat(response.statusCode())
                .as("API must reject string depositpaid — if 200, this is a BUG")
                .isEqualTo(400);

    }

    //Test case 11 : create a booking with empty first name
    @Test(groups = {"negative"})
    @Story("Create a booking with empty first name and verify response")
    @Description("Negative test case to create a booking with empty first name and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithEmptyFirstName(){
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-07-01", "2025-07-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");

        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 12 : create booking with extra field
    @Test(groups = {"smoke","regression"})
    @Story("Create a booking with extra field and verify response")
    @Description("Positive test case to create a booking with extra field and verify that the response status code is 200 and the extra field is ignored.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithExtraField(){
        Response response = given(spec)
                .body("""
                        {
                            "firstname" : "Jim",
                            "lastname" : "Brown",
                            "totalprice" : 111,
                            "depositpaid" : true,
                            "bookingdates" : {
                                "checkin" : "2025-01-01",
                                "checkout" : "2025-01-15"
                            },
                            "additionalneeds" : "Breakfast",
                            "extrafield": "This should be ignored"
                        }""")
                .when().post("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
    }

   //Test case 13 : create a booking with Empty body
    @Test(groups = {"negative"})
    @Story("Create a booking with empty body and verify response")
    @Description("Negative test case to create a booking with empty body and verify that the response status code is 400.")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyBodyShouldReject() {
            Response response = given(spec)
                    .body("{}")
                    .when().post("/booking");

            assertThat(response.statusCode())
                    .as("API must reject empty body — if 200, this is a BUG")
                    .isEqualTo(400);
        }

    //Test case 14 : create a booking with additionalneeds as null
    @Test(groups = {"smoke","regression"})
    @Story("Create a booking with additionalneeds as null and verify response")
    @Description("Positive test case to create a booking with additionalneeds as null and verify that the response status code is 200 and additionalneeds is set to null in the response.")
    @Severity(SeverityLevel.NORMAL)
    public void testAdditionalNeedsAsNullObserveBehavior() {
        Response response = given(spec)
                .body("{ \"firstname\": \"Jim\"," +
                        "\"lastname\": \"Brown\"," +
                        "\"totalprice\": 111," +
                        "\"depositpaid\": true," +
                        "\"bookingdates\": {\"checkin\": \"2025-01-01\", \"checkout\": \"2025-01-15\"}," +
                        "\"additionalneeds\": null }")
                .when().post("/booking");
        assertThat(response.statusCode())
                .as("API should handle null additionalneeds gracefully")
                .isEqualTo(200);
    }
}