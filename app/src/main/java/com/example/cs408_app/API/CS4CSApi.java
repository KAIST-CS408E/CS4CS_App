package com.example.cs408_app.API;
import com.example.cs408_app.Model.Alarm;
import com.example.cs408_app.Model.AlarmElement;
import com.example.cs408_app.Model.Announce;
import com.example.cs408_app.Model.AnnounceElement;
import com.example.cs408_app.Model.Comment;
import com.example.cs408_app.Model.CommentElement;
import com.example.cs408_app.Model.Response;
import com.example.cs408_app.Model.User;
import com.example.cs408_app.Model.UserProfile;

import java.util.List;

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

    @POST("/alarm/report")
    Call<Response> postAlarm(@Body Alarm alarm);

    @GET("/alarm/get_list")
    Call<List<AlarmElement>> getAlarmList();

    @GET("/alarm/reporter/{alarm_id}")
    Call<UserProfile> getReporterProfile(@Path("alarm_id") String alarm_id);

    /* Comment API */
    @POST("/comment/make/{alarm_id}")
    Call<Response> makeComment(@Body Comment comment, @Path("alarm_id") String alarm_id);

    @GET("/comment/get_list/{alarm_id}")
    Call<List<CommentElement>> getCommentList(@Path("alarm_id") String alarm_id);

    @POST("/comment/make_reply/{alarm_id}/{comment_id}")
    Call<Response> makeReply(@Body Comment comment, @Path("alarm_id") String alarm_id, @Path("comment_id") String comment_id);

    @GET("/comment/get_list_reply/{alarm_id}/{comment_id}")
    Call<List<CommentElement>> getReplyList(@Path("alarm_id") String alarm_id, @Path("comment_id") String comment_id);

    /* Announce API */
    @POST("/announce/make/{alarm_id}")
    Call<Response> makeAnnounce(@Body Announce announce, @Path("alarm_id") String alarm_id);

    @GET("/announce/get_list/{alarm_id}")
    Call<List<AnnounceElement>> getAnnounceList(@Path("alarm_id") String alarm_id);

}