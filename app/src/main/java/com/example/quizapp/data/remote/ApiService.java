package com.example.quizapp.data.remote;

import com.example.quizapp.data.remote.model.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // -- AUTH --
    @POST("auth/register")
    Call<GenericResponse<AuthData>> register(@Body RegisterRequest body);

    @POST("auth/login")
    Call<GenericResponse<AuthData>> login(@Body LoginRequest body);

    @POST("auth/google")
    Call<GenericResponse<AuthData>> googleLogin(@Body GoogleLoginRequest body);

    @POST("auth/forgot-password")
    Call<GenericResponse<Void>> forgotPassword(@Body ForgotPasswordRequest body);

    @POST("auth/reset-password")
    Call<GenericResponse<Void>> resetPassword(@Body ResetPasswordRequest body);



    // -- QUIZ --
    @GET("quiz/categories")
    Call<GenericResponse<List<Category>>> getCategories();

    @GET("quiz/questions/{categoryId}")
    Call<GenericResponse<List<Question>>> getQuestions(
            @Path("categoryId") int categoryId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @POST("quiz/submit")
    Call<GenericResponse<Void>> submitScore(@Body SubmitScoreRequest body);

    @GET("quiz/leaderboard")
    Call<GenericResponse<List<LeaderboardEntry>>> getLeaderboard(
            @Query("period") String period,
            @Query("page") int page
    );

    // -- USER --
    @GET("user/stats")
    Call<GenericResponse<UserStats>> getUserStats();

    @GET("user/streak")
    Call<GenericResponse<StreakData>> getStreakData();

    @POST("user/sync")
    Call<GenericResponse<Void>> syncData();

    // -- RESULTS/HISTORY --
    @GET("quiz/history")
    Call<HistoryResponse> getHistory(@Query("page") int page, @Query("limit") int limit);

    @GET("quiz/session/{sessionId}")
    Call<List<SessionAnswer>> getSessionDetail(@Path("sessionId") int sessionId);

    @POST("quiz/submit-detailed")
    Call<ResultResponse> submitDetailedResult(@Body SubmitResultRequest body);

    @GET("quiz/favourites")
    Call<GenericResponse<List<Question>>> getFavourites();

    @POST("quiz/favourites")
    Call<GenericResponse<MessageResponse>> addFavourite(@Body AddFavouriteRequest body);

    @DELETE("quiz/favourites/{questionId}")
    Call<GenericResponse<MessageResponse>> removeFavourite(@Path("questionId") int questionId);

    @GET("notifications")
    Call<GenericResponse<List<Notification>>> getNotifications(@Query("page") int page);

    // -- ADMIN --
    @GET("admin/questions")
    Call<GenericResponse<List<Question>>> getAllQuestions(@Query("page") int page);

    @POST("admin/questions")
    Call<GenericResponse<Question>> addQuestion(@Body Question question);

    @PUT("admin/questions/{id}")
    Call<GenericResponse<Question>> updateQuestion(@Path("id") int id, @Body Question question);

    @DELETE("admin/questions/{id}")
    Call<GenericResponse<Void>> deleteQuestion(@Path("id") int id);
}

