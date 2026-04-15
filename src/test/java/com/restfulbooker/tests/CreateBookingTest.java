package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.BookingRequest;
import com.restfulbooker.requests.BookingRequests;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBookingTest extends BaseTest {
    //Test case 1 : Create a booking with valid data and verify that the response status code is 200 and the response body contains the booking ID and the correct first name.
    @Test
    public void createBookingTestReturns200() {
        BookingRequests api = new BookingRequests(spec);
        BookingDates bookingDates = new BookingDates("2025-07-01", "2025-07-10");
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(bookingDates);
        payload.setAdditionalneeds("Breakfast");
        String firstName = payload.getFirstname();
        Response response = api.createBooking(payload);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("bookingid")).isGreaterThan(0);
        assertThat(response.jsonPath().getString("booking.firstname")).isEqualTo(firstName);


    }

    //Test case 2 : Create a booking and verify that the response body contains the same data as the request.
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testCreateBookingCheckoutBeforeCheckin() {
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(new BookingDates("2025-12-31", "2025-01-01"));

        Response response = api.createBooking(payload);

        System.out.println("Checkout before checkin status: " + response.statusCode());
        System.out.println("Checkout before checkin body: " + response.asString());
        assertThat(response.statusCode()).isEqualTo(400);
    }
    //Test case 8 : create a booking with checkin = checkout date.
    @Test
    public void testCheckinEqualsCheckout(){
        BookingRequests api = new BookingRequests(spec);
        BookingRequest payload = new BookingRequest();
        payload.setFirstname("Jim");
        payload.setLastname("Brown");
        payload.setTotalprice(111);
        payload.setDepositpaid(true);
        payload.setBookingdates(new BookingDates("2025-01-01", "2025-01-01"));

        Response response = api.createBooking(payload);


        System.out.println("Checkin equals checkout status: " + response.statusCode());
        System.out.println("Checkin equals checkout body: " + response.asString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 9 : create a booking with invalid checkin format.
    @Test
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
        System.out.println("Invalid checkin format status: " + response.statusCode());
        System.out.println("Invalid checkin format body: " + response.asString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

    //Test case 10 : create a booking with depositpaid as String.
    @Test
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
        System.out.println("Depositpaid as string status: " + response.statusCode());
        System.out.println("Depositpaid as string body: " + response.asString());
        assertThat(response.statusCode())
                .as("API must reject string depositpaid — if 200, this is a BUG")
                .isEqualTo(400);

    }

    //Test case 11 : create a booking with empty first name
    @Test
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
    @Test
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
        System.out.println("Create booking with extra field status: " + response.statusCode());
        System.out.println("Create booking with extra field body: " + response.asString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

   //Test case 13 : create a booking with Empty body
    @Test
    public void testEmptyBodyShouldReject() {
            Response response = given(spec)
                    .body("{}")
                    .when().post("/booking");

            assertThat(response.statusCode())
                    .as("API must reject empty body — if 200, this is a BUG")
                    .isEqualTo(400);
        }

    //Test case 14 : create a booking with additionalneeds as null
    @Test
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

