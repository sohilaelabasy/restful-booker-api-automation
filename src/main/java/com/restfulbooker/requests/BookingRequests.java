package com.restfulbooker.requests;
import com.restfulbooker.models.BookingRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import static io.restassured.RestAssured.given;

public class BookingRequests {
    private final RequestSpecification spec ;

    public BookingRequests(RequestSpecification spec) {
        this.spec = spec;
    }
    public Response createBooking(BookingRequest payload){
        return given(spec)
                .body(payload)
                .when().post("/booking");
    }
    public Response getBooking(int id){
        return
                given(spec)
                        .when().get("/booking/"+id);
    }
    public Response getAllBookings(){
        return given(spec)
                .when().get("/booking");
    }
    public Response updateBooking(int id , BookingRequest payload , String token){
        return given(spec)
                .cookie("token",token)
                .body(payload)
                .when().put("/booking/"+id);
    }
    public Response deleteBooking(int id  , String token){
        return given(spec)
                .cookie("token",token)
                .when().delete("/booking/"+id);
    }
    public Response partialUpdateBooking(int id , Object payload , String token){
        return given(spec)
                .cookie("token" , token)
                .body(payload)
                .when().patch("/booking/"+ id);
    }

}
