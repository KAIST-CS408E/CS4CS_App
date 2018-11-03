package com.example.cs408_app.API;
import com.example.cs408_app.Model.Response;
import com.example.cs408_app.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CS4CSApi {

    /*
    Interface form: OutputType FunctionName(InputType InputName, ...);
    To request some action from the server, you need:
    1. url
    2. Http verb(POST, GET, etc)
    3. header + body + query
    4. etc
     */

    /*
    To register, the User object is required as an input
    In this case, User object is the body of this request
    Call<T>: T is response(output) body type
     */
    @POST("/users") // This annotation represents a url and Http verb
    Call<Response> register(@Body User user);

    /*
    To get a profile, String email is required as an input
    In this case, you can use the email String and {} notation to access URLs dynamically.
    the email String represents the last part of url; /users/"{email}"
     */
    @GET("/users/{email}")
    Call<User> getProfile(@Path("email") String email);

    @GET("/")
    Call<Response> welcomeMsg();
}