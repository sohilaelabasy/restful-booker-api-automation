package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class FilterBookingTest extends BaseTest {
        // Test Case 1 — Filter by firstname
        @Test
        public void testFilterByFirstname(){
        Response response = given(spec)
                .queryParam("firstname" ,"Jim")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
       }


        // Test Case 2 — Filter by lastname
        @Test
        public void testFilterByLastname(){
        Response response = given(spec)
                .queryParam("lastname", "Brown")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
        }

        // Test Case 3 — Filter by checkin date
        @Test
         public void testFilterByCheckinDate(){
        String filterDate = "2025-07-01";
        Response response = given(spec)
                .queryParam("checkin", filterDate)
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isNotEmpty();
        }

        // Test Case 4 — Filter by checkout date
        @Test
        public void testFilterByCheckout() {
            String filterDate = "2024-01-01";

            Response response = given(spec)
                    .queryParam("checkout", filterDate)
                    .when().get("/booking");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getList("$")).isNotEmpty();
        }
    @Test
    public void testFilterByCheckinAndCheckout() {

        String checkinDate = "2014-03-13";
        String checkoutDate = "2014-05-21";

        Response response = given(spec)
                .queryParam("checkin", checkinDate)
                .queryParam("checkout", checkoutDate)
                .when().get("/booking");

        List<Integer> ids = response.jsonPath().getList("bookingid");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(ids).isNotEmpty();

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
        // Test Case 5 —   Filter with no matching results
        @Test
        public void testFilterWithNoMatchingResults() {
        Response response = given(spec)
                .queryParam("firstname", "ggsgghaajjwjw")
                .when().get("/booking");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$")).isEmpty();
        }

        // Test Case 6 — Filter with invalid date format
        @Test
        public void testFilterWithInvalidDateFormat() {
            Response response = given(spec)
                    .queryParam("checkin", "not-a-date")
                    .when().get("/booking");
            assertThat(response.statusCode()).isEqualTo(400);
        }

        //Test Case 7 : Checkout before checkin in filter
        @Test
    public void testFilterWithCheckoutBeforeCheckin(){
            Response response =given(spec)
                    .queryParam("checkin" , "2025-12-31")
                    .queryParam("checkout" , "2025-01-02")
                    .when()
                    .get("/booking");
            System.out.println("Checkout before checkin filter status: " + response.statusCode());
            System.out.println("Checkout before checkin filter body: " + response.asString());
            assertThat(response.statusCode()).isEqualTo(200);
        }

        //Test case 8 : SQL injection in filter
        @Test
    public void testFilterWithSQLInjectionInFirstname(){
            Response response = given(spec)
                    .queryParam("firstname" , "Jim' OR '1' = '1'")
                    .when().get("/booking");
            assertThat(response.statusCode()).isIn(400 , 200);
        }

        //Test case 9 :
    @Test
    public void testFilterWithTooLongValue() {
        Response response = given(spec)
                .queryParam("firstname", "A".repeat(500))
                .when().get("/booking");
        assertThat(response.statusCode()).isNotEqualTo(500);
    }

}
