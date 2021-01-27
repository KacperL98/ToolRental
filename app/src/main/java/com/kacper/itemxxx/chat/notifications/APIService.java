package com.kacper.itemxxx.chat.notifications;

import com.kacper.itemxxx.chat.model.Data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key = AAAARTDhuT8:APA91bH6Cmscj65A7590TBGT0c3Drl5CVJHMz_xIWcV45YMI6q-a6yXqvmv0e8w8Qmpe7nVJGYpJukxsGwShT9peujY1mkW0_lIHYuq8vAR-LMUFH48X6AJrj0zOKyf3d_wc6Ccr8Skv"
    })
    @POST("fcm/send")
    Call<Data> sendNotification(@Body Sender body);
}